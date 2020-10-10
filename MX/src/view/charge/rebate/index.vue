<template>
  <div class="box_col">
    <Row>
      <Col span="24">
        <Menu mode="horizontal" theme="light" :active-name="MenuItemName"
              @on-select="keerkesan"
              style="font-size: 48px;font-weight: bold">
          <MenuItem name="1">
            待返点
          </MenuItem>
          <MenuItem name="2">
            已返点
          </MenuItem>
        </Menu>
      </Col>
    </Row>
    <div class="body" v-if="MenuItemName=='1'">
      <Row style="padding: 12px 0;padding-right: 10px;display: flex;justify-content: flex-end" :gutter="6">
        <Col span="6">
        </Col>
        <Col span="18" style="display: flex;justify-content: flex-end">
          <Col span="5" style="margin-right: 10px">
            <Input id="code" ref="code" :autofocus="true" v-focus v-model="param.idLike" clearable placeholder="请扫描条形码" @on-enter="getOldData"/>
          </Col>
          <Col span="4">
            <Input v-model="param.jlXmLike" clearable placeholder="教练员姓名" @on-enter="getOldData"/>
          </Col>
          <Col span="1" style="margin-right: 20px">
          <span style="margin:0 10px;">
            <Button type="primary" @click="getOldData">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </span>
          </Col>
          <Col span="2" style="margin-right: 10px">
            <span style="margin-left: 10px"><Button type="primary" @click="confirm">确认返点</Button></span>
          </Col>
        </Col>
      </Row>
      <Table :height="AF.getPageHeight()-250" stripe size="small" @on-select="tabsel" @on-select-cancel="tabsel"
             @on-select-all="tabselAll" @on-select-all-cancel="tabselAll1" @on-selection-change="tabselAll"
             :columns="tableColumns" :data="tableData"></Table>
      <Row>
        <Col span="4">
          <div style="text-align: center;padding: 6px 0;display: flex;justify-content: flex-end">
             <span style="font-size: 15px;font-weight: 600">
            小计：<span style="color: #ed3f14"> {{okParams.fdJe}} </span> 元
            </span>
          </div>
        </Col>
        <Col span="20">
          <div style="text-align: right;padding: 6px 0;display: flex;justify-content: flex-end">

        <span style="font-size: 12px;padding-top: 7px">
          <span>共&nbsp; </span>
          <span>{{hj}}</span>
          <span> &nbsp;元</span>
          </span>
            <Page :total=totalS
                  :current=param.pageNum
                  :page-size=param.pageSize
                  :page-size-opts=[8,10,15,20,30,40,50]
                  show-total
                  show-elevator
                  show-sizer
                  placement='top'
                  style="display: inline-block;margin-left: 20px"
                  @on-page-size-change='(n)=>{pageSizeChange(n)}'
                  @on-change='(n)=>{pageChange(n)}'>
            </Page>
          </div>
        </Col>
      </Row>

    </div>
    <div class="body" v-else-if="MenuItemName=='2'">
      <ok-back></ok-back>
      <div v-if="false">
        <Row style="padding: 12px 0" :gutter="12">
          <Col span="4">
            <Input id="code" autofocus v-model="param.idLike" placeholder="请扫描条形码" @on-enter="getOldData"/>
          </Col>
          <Col span="4">
            <Input autofocus v-model="param.jlXmLike" placeholder="教练员姓名" @on-enter="getOldData"/>
          </Col>
          <Col span="8">
          <span style="margin-left: 16px;">
            <Button type="primary" @click="getOldData">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </span>
            <span style="color: red;font-weight: 600;font-size: 20px;padding-left: 16px">
            <span>合计：</span>
            <span>{{okParams.fdJe}}元</span>
            <span style="margin-left: 16px"><Button type="error" @click="confirm">确认</Button></span>
          </span>

          </Col>
        </Row>
        <Table :height="AF.getPageHeight()-250" stripe size="small"
               :columns="tableColumns" :data="tableData"></Table>
      </div>
    </div>
    <component :is="componentName" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  import remark from './remark'
  import okBack from './okBack'
  import printSignUp from './comp/printSignUp'

  export default {
    name: "index",
    components: {remark, okBack, printSignUp},
    watch: {},
    directives: {
      focus:function (el) {
        el.focus();
      }
    },
    data() {
      return {
        input: '',
        jlLxList:[ {
          value: '00',
          label: '本校'
        },
          {
            value: '10',
            label: '外校'
          },],
        hisPrintMess: {},
        componentName: '',
        choosedItem: null,
        count: 9000,
        count1: 200000,
        MenuItemName: '1',
        tableData: [],
        tableColumns: [
          {title: '序号', type: 'index', minWidth: 60, align: 'center'},
          {
            title: '#',
            type: 'selection',
            width: 60,
            align: 'center'
          },
          {title: '凭证号', key: 'id', minWidth: 170, align: 'center'},
          {
            title: '科目', align: 'center', key: 'lcKm', minWidth: 120,
            render: (h, p) => {
              if (p.row.lcKm == '2') {
                return h('div', '科目二')
              } else {
                return h('div', '科目三')
              }
            },
            filters: [
              {
                label: '科目二',
                value: '2'
              },
              {
                label: '科目三',
                value: '3'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              // var _self =  this.$options.parent.parent
                this.param.lcKm=value
              this.getOldData()

             // return value==row.lcKm
            }
          },
          {title: '驾校', align: 'center', key: 'jlJx', minWidth: 120,
            filters: [
              {
                label: '本校',
                value: '00'
              },
              {
                label: '外校',
                value: '10'
              }
            ],
            filterMultiple: false,
            filterRemote (value, row) {
              this.param.jlLx=value
              this.getOldData()
            }
          },
          {title: '教练员', align: 'center', key: 'jlXm', minWidth: 80},
          {
            title: '费用', align: 'center', key: 'lcFy', minWidth: 80,
            render: (h, p) => {
              return h('div', p.row.lcFy + '元')
            }

          },
          {
            title: '返点类型', align: 'center', key: 'fdlx', minWidth: 120,
            render: (h, p) => {
              if (p.row.fdlx == '00') {
                return h('div', '计时返点')
              } else if (p.row.fdlx == '10') {
                return h('div', '按把返点')
              }else if(p.row.fdlx == '20'){

                return h('div',"培优返点")
              } else {
                return h('div', '开放日返点')
              }
            }
          },
          {
            title: '返点金额', align: 'center', key: 'fdje', minWidth: 80,
            render: (h, p) => {
              return h('div', p.row.fdje + '元')
            }
          },

          {
            title: '备注', align: 'center', key: 'bz', minWidth: 160,
            render: (h, p) => {
              return h('div', p.row.bz)
            }
          },
          {title: '创建时间', align: 'center', key: 'cjsj', minWidth: 140,
            render:(h,p)=>{
              return h('div',p.row.cjsj.substring(0,10))
            }
          },
        ],
        total: 0,
        ids: '',
        scanning: false,
        totalS: 0,
        param: {
          qrsjIsNull: '1',
          orderBy: 'cjsj desc',
          idLike: '',
          jlLx:'',
          lcKm:'',
          jlXmLike: '',
          pageNum: 1,
          pageSize: 15
        },
        okParams: {
          jl:'',
          id: '',
          fdJe: 0
        },
        hj:0
      }
    },
    created() {
      this.getOldData();
    },
    methods: {
      getFdjl() {
        this.$http.post('/api/bizlcfd/pager', this.param).then((res) => {
          if (res.code == 200) {

          }
        })
      },
      pageChange(val) {
        this.param.pageNum = val
        this.getOldData();
      },
      pageSizeChange(val) {
        this.param.pageSize = val
        this.getOldData();
      },
      keerkesan(val) {
        this.MenuItemName = val
        if (val == 1) {
          this.param.qrsjIsNull = '1';
          this.param.orderBy = 'cjsj desc';
          this.param.lcKm = '';
          this.param.jlLx ='';
          this.param.jlXmLike = '';
          this.param.idLike = '';
          this.param.pageNum = 1
          this.param.pageSize = 15;
          delete this.param.qrsjIsNotNull
          this.getOldData();
        }
        if (val == 2) {
          this.param.qrsjIsNotNull = '1';
          this.param.orderBy = 'qrsj desc';
          this.param.lcKm = '';
          this.param.jlLx ='';
          this.param.jlXmLike = '';
          this.param.idLike = '';
          this.param.pageNum = 1
          this.param.pageSize = 15;
          delete this.param.qrsjIsNull;

          this.total = 0;
          this.ids = '';
        }
      },
      getOldData() {
        this.total = 0;
        this.ids = '';
        this.hj=0;
        this.$http.post('/api/bizlcfd/getPager', this.param).then((res) => {
          if (res.code == 200 && res.page.list) {
            this.totalS = res.page.total
            this.tableData = res.page.list;
            this.hj = res.result
            this.param.idLike=''
            this.$refs['code'].focus();
          }
        })
      },
      add() {
        if (this.scanning || this.input.length > 32) {
          return;
        }
        let v = this;
        v.scanning = true;
        setTimeout(() => {
          v.scanning = false;
        }, 800)
        for (let r of this.tableData) {
          if (r.id == this.param.idLike) {
            this.param.idLike = '';
            return;
          }
        }
        this.$http.post(this.apis.lcjl.getFdZt, {id: this.param.idLike, notShowLoading: 'true'}).then((res) => {
          if (res.code == 200) {
            if (res.result.length != 0) {
              this.tableData.push(res.result);
              if (res.result.fdZt == '00' || res.result.fdZt == '20') {
                this.total += res.result.lcFy;
              }
              this.ids += res.result.id + ','
            }
            this.scanning = false;
          } else {
            this.$Message.error(res.message)
          }
          this.param.idLike = '';
        })
      },
      tabselAll1(){
        this.okParams.id = ''
        this.okParams.fdJe = 0;
        console.log(this.okParams.fdJe,'this.okParams.fdJe');
        this.$nextTick()
      },
      tabselAll(list){
        this.okParams.id = ''
        this.okParams.fdJe = 0
        this.okParams.jl = list[0].jlXm
        list.forEach((it, index) => {
          this.okParams.fdJe = this.okParams.fdJe + it.fdje
          if (index == list.length - 1) {
            this.okParams.id = this.okParams.id + it.id

          } else {
            this.okParams.id = this.okParams.id + it.id + ','
          }
          console.log(this.okParams.fdJe,'this.okParams.fdJe');
        })
      },
      tabsel(list, row) {
        console.log(list, row)

        if (row != undefined && list[0].jlId !== row.jlId) {
          this.$Message.error('选择的教练并非同一位')
        }
        this.okParams.id = ''
        this.okParams.fdJe = 0
        this.okParams.jl = row.jlXm
        list.forEach((it, index) => {
          this.okParams.fdJe = this.okParams.fdJe + it.fdje
          if (index == list.length - 1) {
            this.okParams.id = this.okParams.id + it.id

          } else {
            this.okParams.id = this.okParams.id + it.id + ','
          }
        })
      },
      confirm() {
        if (this.okParams.id == '') {
          this.swal({
            title: '请选择返点数据',
            type: 'warning'
          })
          return
        }
        this.swal({
          title: '是否确认[ '+this.okParams.jl+' ]返点[ '+this.okParams.fdJe+' ]元?',
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '关闭',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/bizlcfd/updateZt', {id: this.okParams.id}).then(res => {
              if (res.code == 200) {
                this.hisPrintMess = res.result
                this.componentName = 'printSignUp'
                this.okParams.fdJe = 0
                this.getOldData();
              }
            }).catch(err => {
            })
          } else {
          }
        })
      }
    }
  }
</script>

<style scoped>


</style>
