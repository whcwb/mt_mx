<style lang="less">
  @import '../../../assets/css/wdPageSty';
</style>
<!--用户管理-->
<template>
  <div class="boxbackborder box_col">
    <Menu mode="horizontal" active-name="1">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          用户管理
        </div>
      </MenuItem>
    </Menu>
    <div class="box_row colCenter rowRight pageFindSty" style="border: none;padding: 8px 0;">
      <div>
        <Select v-model="param.jgdm" placeholder="请选择考场" clearable @on-change="findMessList">
          <Option v-for="item in JG" :value="item.jgdm">{{ item.jgmc }}</Option>
        </Select>
      </div>
      <div>
        <Icon type="md-person" size="34"/>
        <Input v-model="param.xmLike"
               placeholder="请输入用户姓名" style="width: 200px"
               @on-keyup.enter="findMessList()"
               @on-change="findMessList"></Input>
      </div>
      <div>
        <Icon type="ios-call" size="34"/>
        <Input v-model="param.sjhLike"
               placeholder="请输入手机号码" style="width: 200px"
               @on-keyup.enter="findMessList()"
               @on-change="findMessList"></Input>
      </div>
      <Button type="primary" @click="findMessList()" style="margin: 0">
        <Icon type="md-search"></Icon>
        <!--查询-->
      </Button>
      <Button type="primary" @click="AddDataList()">
        <Icon type="md-add"></Icon>
      </Button>
    </div>

    <Row style="position: relative;">
      <Table
        size="small" stripe
        :height="AF.getPageHeight()-270"
        :row-class-name="rowClassName"
        :columns="tableTiT"
        :data="tableData"></Table>
    </Row>
    <Row class="margin-top-10 pageSty">
      <Page :total=pageTotal :current=param.pageNum :page-size=param.pageSize
            :page-size-opts=[8,10,20,30,40,50] @on-page-size-change='(e)=>{param.pageSize=e;pageChange()}'
            show-total show-elevator show-sizer placement='top' @on-change='pageChange'></Page>
    </Row>
    <component
      :is="compName"
      :usermes="usermes"
      :usermesType="userMesType"
      @listF='listF'></component>
  </div>
</template>

<script>
import mixins from '@/mixins'
import ukey from './comp/ukey.vue'
import newmess from './comp/newmes.vue'
import changemes from './comp/changmes.vue'

export default {
  name: 'char',
  components: {
    ukey,
    newmess,
    changemes
  },
  mixins: [mixins],
  data() {
    return {
      JG: [],
      JGList: {},
      //tab高度
      tabHeight: 220,
      //动态组建
      compName: '',
      //动态组建数据
      usermes: {},
      userMesType: true,
      //分页
      //---数据总数
      pageTotal: 2,
        tableTiT: [{
          title: "序号",
          width: 80,
          align: 'center',
          type: 'index'
        },
          {
            title: '帐号',
            align: 'center',
            key: 'zh'
          },
          {
            title: '姓名',
            align: 'center',
            key: 'xm'
          },
          {
            title: '证件号码',
            align: 'center',
            key: 'zjhm'
          },
          {
            title: '性别',
            align: 'center',
            key: 'xb',
            render: (h, params) => {
              return h('div', params.row.xb == '10' ? '男' : '女')
            }
          },
          {
            title: '手机号',
            width: 120,
            align: 'center',
            key: 'sjh'
          },
          {
            title: '职务',
            align: 'center',
            key: 'zw'
          },
          {
            title: '类型',
            align: 'center',
            key: 'lx',
            render: (h, p) => {
              let val = this.dictUtil.getValByCode(this, this.yhlxDictCode, p.row.lx)
              let key = this.JGList.get(p.row.jgdm)
              return h('div', key + '/' + val)
            }
          },
          {
            title: '操作',
            key: 'action',
            width: 180,
            align: 'center',
            render: (h, params) => {
              if (params.row.lx === 'su') {
                return ''
              }
              return h('div', [
                h('Button', {
                  props: {
                    type: 'success',
                    icon: 'md-key',
                    shape: 'circle',
                    size: 'small'
                  },
                  style: {
                    cursor: "pointer",
                    margin: '0 8px 0 0'
                  },
                  on: {
                    click: () => {
                      this.userMesType = false
                      this.usermes = params.row
                      this.compName = 'ukey'
                    }
                  }
                }),
                h('Button', {
                  props: {
                    type: 'success',
                    icon: 'md-create',
                    shape: 'circle',
                    size: 'small'
                  },
                  style: {
                    cursor: "pointer",
                    margin: '0 8px 0 0'
                  },
                  on: {
                    click: () => {
                      this.userMesType = false
                      this.usermes = params.row
                      this.compName = 'newmess'
                    }
                  }
                }),
                h('Button', {
                  props: {
                    type: 'primary',
                    icon: 'md-menu',
                    shape: 'circle',
                    size: 'small'
                  },
                  style: {
                    cursor: "pointer",
                    margin: '0 8px 0 0'
                  },
                  on: {
                    click: () => {
                      this.RootShowF(params.row)

                    }
                  }
                }),
                h('Button', {
                  props: {
                    type: 'error',
                    icon: 'md-close',
                    shape: 'circle',
                    size: 'small'
                  },
                  style: {
                    cursor: "pointer",
                    margin: '0 8px 0 0'
                  },
                  on: {
                    click: () => {
                      this.listDele(params.row.yhid)
                    }
                  }
                })
              ]);
            }
          }
        ],
      tableData: [],
      //收索
      param: {
        sjhLike: '',
        xmLike: '',
        timers: '',
        pageNum: 1,
        pageSize: 8,
        jgdm: ''
      },
      yhlxDict: [],
      yhlxDictCode: 'ZDCLK0003',
    }
    },
    watch: {},
    created() {
      this.getJgs();
      this.getmess()
      this.getDict()
    },
    methods: {
      getJgs() {
        this.JGList = new Map()
        this.$http.get("/api/jg/query").then(res => {
          this.JG = res.result
          for (let val of res.result) {
            this.JGList.set(val.jgdm, val.jgmc)
          }
        })
      },
      getDict() {
        this.yhlxDict = this.dictUtil.getByCode(this, this.yhlxDictCode);
        console.log(this.yhlxDict);
      },
      getmess() {
        var v = this
        this.param.timers = new Date().getTime()
        this.$http.get(this.apis.USER.QUERY, {params: v.param}).then((res) => {
          v.tableData = res.page.list
          v.pageTotal = res.page.total
        })
      },
      //权限分配
      RootShowF(val) {
        var v = this
        v.compName = 'changemes'
        this.usermes = val;
      },
      //收索事件
      findMessList() {
        var v = this
        this.param.timers = new Date().getTime()
        this.$http.get(this.apis.USER.QUERY, {params: v.param}).then((res) => {
          v.tableData = res.page.list
        })
      },
      //添加新用户信息
      AddDataList() {
        var v = this
        v.compName = 'newmess'
        v.userMesType = true
        this.usermes = null
      },
      listF() {
        this.getmess()
        this.compName = ''
      },
      //删除数据
      listDele(id) {
        this.util.del(this, this.apis.USER.DELE, [id], () => {
          this.getmess();
        });
      },
      //分页点击事件按
      pageChange(event) {
        var v = this
        v.param.pageNum = event
        v.getmess()
      }
    }
  }
</script>
