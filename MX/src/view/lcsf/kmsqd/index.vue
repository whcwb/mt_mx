<template>
  <div class="boxbackborder box_col">
    <!--<pager-tit title="安全员签到"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          安全员签到
        </div>
      </MenuItem>
    </Menu>
    <div style="text-align: right">
      <!--<Input v-model="param.xmLike" placeholder="请输入姓名" style="width: 200px;margin-right: 10px;"></Input>-->
      <!--<Button type="primary" @click="getData">-->
      <!--<Icon type="md-search"></Icon>-->
      <!--</Button>-->
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='',getData()">总计{{total}}人</span>
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='10',getData()">已签到{{yqdNum}}人</span>
      <span
        style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
        @click="param.aqyQdzt='00',getData()">未签到{{wqdNum}}人</span>
      <Button style="margin-left: 10px;" type="primary" @click="modalTitle='新增安全员',showModal=true">
        <Icon type="md-add"></Icon>
      </Button>
      <Button type="primary" @click="param.aqyQdzt='',getData()" style="margin-left: 10px">
        <Icon type="md-refresh"/>
        <!--查询-->
      </Button>
    </div>
    <!--<div class="box_col_auto">-->
    <!--<div class="box_row_list" >-->
    <!--<div  v-for="(item,ri) in pageData" :key="ri" style="width: 20%;padding: 8px;">-->
    <!--<Card>-->
    <!--<div slot="title">-->
    <!--<div class="number" :style="{background:item.rzt?'#19be6b':'#ed4014',color:'#fff'}">{{item.idx}}</div>-->
    <!--</div>-->
    <!--<div slot="extra">-->
    <!--<i-switch v-model="item.rzt" @on-change="change(item)" :disabled="item.km == '2'">-->
    <!--<span slot="open">签到</span>-->
    <!--<span slot="close">休息</span>-->
    <!--</i-switch>-->
    <!--</div>-->
    <!--<div style="text-align: center" class="box_row colBottom"><h2>{{item.xm}}</h2>__<Icon type="md-call" size="22"/><h3>{{item.sjhm}}</h3></div>-->
    <!--</Card>-->
    <!--</div>-->
    <!--</div>-->
    <!--</div>-->
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-210" :pager="false"></table-area>

    <Modal
      v-model="showModal"
      height="400"
      width="900"
      :closable='false'
      :mask-closable="false"
      :title="modalTitle">
      <Form
        ref="form"
        :label-width="100"
        :styles="{top: '20px'}">
        <div style="overflow: auto;width:800px">
          <Row style="display: flex;justify-content: space-between">
            <Col span="10">
              <FormItem label="安全员姓名">
                <Input v-model="AQY.xm" size="large" placeholder="请输入安全员姓名"/>
              </FormItem>
            </Col>
            <Col span="10">
              <FormItem label="安全员联系电话">
                <Input v-model="AQY.sjhm" size="large" placeholder="请输入安全员联系电话"/>
              </FormItem>
            </Col>
          </Row>
          <Row style="display: flex;justify-content: space-between">
            <Col span="10">
              <FormItem label="准驾车型">
                <Input v-model="AQY.zjcx" size="large" placeholder="请输入准驾车型"/>
              </FormItem>
            </Col>
            <Col span="10">
              <FormItem label="安全员证号">
                <Input v-model="AQY.jlzh" size="large" placeholder="请输入安全员证号"/>
              </FormItem>
            </Col>
          </Row>
        </div>
      </Form>
      <div slot='footer'>
        <Button type="default" @click="AQY={},showModal=false" style="color: #949494">取消</Button>
        <Button v-if="modalTitle=='更改安全员信息'" type="error" @click="del">删除</Button>
        <Button type="primary" @click="SaveAQY">确定</Button>
      </div>
    </Modal>

  </div>
</template>

<script>

  export default {
    name: 'char',
    components: {},
    data() {
      return {
        v: this,
        apiRoot: this.apis.zgjbxx,
        choosedItem: null,
        componentName: '',
        pageData: [],
        tableColumns: [
          {
            title: '序号', align: 'center', minWidth: 90,
            // render: (h, p) => {
            //   return h('div', {
            //     style: {
            //       fontWeight: '800'
            //     },
            //   }, p.row.idx)
            // }
            render: (h, p) => {
              return h('Tag', {
                props: {
                  type: 'volcano',
                }
              }, p.row.idx)
            }
          },
          {title: '安全员', align: 'center', key: 'xm', minWidth: 80},
          {title: '安全员电话', align: 'center', key: 'sjhm', minWidth: 80},
          {title: '准驾车型', align: 'center', key: 'zjcx', minWidth: 80},
          {title: '驾驶证号', align: 'center', key: 'jlzh', minWidth: 80},
          {
            title: '签到', fixed: 'right', align: 'center', minWidth: 60,
            render: (h, p) => {
              return h('i-switch', {
                  props: {value: p.row.rzt, disabled: p.row.km == '2'},
                  on: {
                    'on-change': (value) => {
                      p.row.rzt = !p.row.rzt
                      this.change(p.row)
                    }
                  }
                }
              )
            }
          },
          {
            title: '操作', fixed: 'right', align: 'center', width: 60, render: (h, p) => {
              let buttons = [];
              buttons.push(
                h('Tooltip',
                  {props: {placement: 'top', content: '维护信息',}},
                  [
                    h('Button', {
                      props: {
                        type: 'warning',
                        shape: 'circle',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0'},
                      on: {
                        click: () => {
                          this.AQY = JSON.parse(JSON.stringify(p.row));
                          this.modalTitle = '更改安全员信息'
                          this.showModal = true
                        }
                      }
                    }, '改')
                  ]
                ),
              )
              return h('div', buttons);
            }
          },
        ],
        specialPageSize: 9999,
        param: {
          zzzt: '10',
          aqybx: '1',
          gzgw: '0005',
          total: 0,
          xmLike: '',
          pageNum: 1,
          pageSize: 9999,
          notShowLoading: 'true',
          aqyQdzt: ''
        },
        yqdNum: 0,
        wqdNum: 0,
        total: 0,
        showModal: false,
        AQY: {},
        modalTitle: ''
      }
    },
    created() {
      // this.getData()
      this.util.initTable(this);
    },
    methods: {
      getData() {
        this.util.getPageData(this)
      },
      change(item) {
        let rzt = item.rzt ? '10' : '00'
        this.$http.post(this.apis.zgjbxx.setaqrqd, {
          'km': '3',
          'id': item.id,
          'aqyQdzt': rzt,
          notShowLoading: 'true'
        }).then((res) => {
          if (res.code == 200) {
            this.getData()
            this.$Message.success(res.message);
          } else {
            this.$set(this.pageData, (item._index).rzt, false);
            this.$Message.error(res.message);
          }
        })
      },
      afterPager(data) {
        let i = 0;
        if (this.param.aqyQdzt === '') this.yqdNum = this.wqdNum = this.total = 0
        for (let r of data) {
          r.rzt = r.aqyQdzt == '10'
          r.idx = ++i;
          if (this.param.aqyQdzt == '') {
            if (r.rzt === true) {
              this.yqdNum++;
            } else {
              this.wqdNum++;
            }
            this.total++;
          }
        }

        this.pageData = data
      },
      SaveAQY() {
        this.AQY.gzgw = '0005'
        this.AQY.km = '3'
        let url = ''
        if (this.modalTitle === '新增安全员')
          url = '/api/zgjbxx/save'
        if (this.modalTitle === '更改安全员信息')
          url = '/api/zgjbxx/update'

        this.$http.post(url, this.AQY).then((res) => {
          if (res.code == 200) {
            this.$Message.success(res.message);
            this.showModal = false
            this.AQY = {}
            this.getData()
          } else {
            this.$Message.error(res.message);
          }
        })
      },
      del() {
        this.swal({
          title: '确定删除该安全员？',
          type: 'warning',
          showCancelButton: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then((val) => {
          if (val.value) {
            this.$http.post('/api/zgjbxx/remove/' + this.AQY.id).then((res) => {
              if (res.code == 200) {
                this.$Message.success(res.message);
                this.showModal = false
                this.AQY = {}
                this.getData()
              } else {
                this.$Message.error(res.message);
              }
            })
          }
        })
      }
    }
  }
</script>
<style>
  .number {
    text-align: center;
    vertical-align: center;
    font-size: 20px;
    padding-top: 10px;
    width: 40px;
    height: 40px;
    border-radius: 20px;
    /*background-color: red;*/
    color: white;
  }
</style>
