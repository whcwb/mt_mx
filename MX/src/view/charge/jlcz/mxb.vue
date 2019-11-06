<template>
  <div>
  <Modal
    v-model="isMxb"
    :title="itemObj.jlXm+' '+itemObj.cardNo+' 明细表'"
    :closable='false'
    :mask-closable='false'
    @on-ok="ok">
    <Table stripe
           :height="AF.getPageHeight()-250"
           :columns="columns2"
           :data="dataList">
    </Table>
    <div style="text-align: right;padding: 6px 0">
      <Page :total=totalS
            :current=param.pageNum
            :page-size=param.pageSize
            :page-size-opts=[8,10,20,30,40,50]
            show-total
            show-elevator
            show-sizer
            placement='top'
            @on-page-size-change='(n)=>{pageSizeChange(n)}'
            @on-change='(n)=>{pageChange(n)}'>
      </Page>
    </div>
    <div slot="footer">
      <Button @click="ok">
        关闭
      </Button>
    </div>
  </Modal>
  <component :is="componentName"  :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  import printSignUp from '../../lcsf/comp/printSignUp'

  export default {
      components: {printSignUp},
    name: "mxb",
    props: {
      itemObj: {
        type: Object,
        default: {}
      },
      isMxb: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        mx: false,
          hisPrintMess:{},
          componentName:'',
        totalS:0,
        param: {
          pageNum: 1,
          pageSize: 8,
          id:''
        },
        dataList: [],
        columns2: [
          {title: '#', type: 'index', width: 60},
          {
            title: '流水时间',
            key: 'cjsj',
            align: 'center',

          },
          {
            title: '类型',
            align: 'center',
            render: (h, p) => {
              let type=''
              switch (p.row.type) {
                case '00':type='开放日预存'
                      break;
                case '10':type='卡片预存'
                  break;
                case '20':type='卡片抵扣'
                  break;
                case '30':type='开放日抵扣'
                  break;
              }
              return h('div', type);
            },
            filters: [
              {
                label: '充值卡预存',
                value: '10'
              },
              {
                label: '充值卡抵扣',
                value: '20'
              },
              {
                label: '开放日预存',
                value: '00'
              },
              {
                label: '开放日抵扣',
                value: '30'
              }
            ],
            filterMultiple: false,
            filterMethod (value, row) {
              // if (value === 1) {
              //   return row.age > 25;
              // } else if (value === 2) {
              //   return row.age < 25;
              // }
              switch (value) {
                case '00':return row.type=='00'
                  break;
                case '10':return row.type=='10'
                  break;
                case '20':return row.type=='20'
                  break;
                case '30':return row.type=='30'
                  break;
              }

            }
          },
          {
            title: '充值金额',
            key: 'je',
            align: 'center',

          },
          {
            title: '实付金额',
            key: 'sfje',
            align: 'center',
          },
          {
            title: '余额',
            align: 'center',
            render: (h, p) => {
              let type=''
              switch (p.row.type) {
                case '00':type='开放日余额:'+p.row.czhje
                  break;
                case '30':type='开放日余额:'+p.row.czhje
                  break;
                case '10':type='充值卡余额:'+p.row.czhje
                  break;
                case '20':type='充值卡余额:'+p.row.czhje
                  break;
              }
              return h('div', type);
            }
          },
          {
            title: '备注',
            key: 'bz',
            align: 'center',
          },
            {
                title: '操作',
                align: 'center',
                render:(h,p)=>{
                    if(p.row.type == '10'){
                        return h('Button', {
                            props: {
                                type: 'info',
                                size: 'small'
                            },
                            style: {
                                borderRadius: '15px'
                            },
                            on:{
                                click:()=>{
                                    this.hisPrintMess = p.row
                                    this.componentName = 'printSignUp'
                                }
                            }
                        }, '打印')
                    }else {
                        return ''
                    }

                }
            }
        ],
      }
    },
    watch: {
      isMxb(val) {
        if (val)
          this.getData()
      },
      deep: true
    },
    methods: {
      getData() {
        this.param.id=this.itemObj.id
        this.$http.post('/api/lcwxjl/czmx', this.param).then(res => {
          if (res.code == 200) {
            this.totalS = res.page.total
            this.dataList = res.page.list
          } else {
            this.swal({
              title: res.message,
              type: 'warning'
            })
          }
        }).catch(err => {
        })
      },
      pageChange(val){
        this.param.pageNum = val
        this.getData();
      },
      pageSizeChange(val){
        this.param.pageSize = val
        this.getData();
      },
      ok() {
        this.$emit('closemxb')
      }
    }
  }
</script>

<style scoped>
  /deep/ .ivu-modal {
    width: 70% !important;
    top: 20px !important;
  }
</style>
