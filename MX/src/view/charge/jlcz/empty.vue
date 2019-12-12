<template>
  <div>
    <Modal
      width="700px"
      title="清空开放日余额"
      v-model="QRmodal"
      :closable="false"
      :mask-closable="false"
      okText="确认"
      cancelText="取消"
      @on-ok="ok"
      @on-cancel="cancel"
    >
      <div>
        <Row>
          <Col>
            <Table size="small" :columns="columns" :data="pageData"
                   @on-select="tabcheck"
                   @on-select-cancel="tabcheck"
                   @on-select-all="tabcheck"
                   @on-select-all-cancel="tabcheck">
            </Table>
          </Col>
        </Row>
      </div>
    </Modal>
  </div>
</template>

<script>
  export default {
    name: "empty",
    props:{
      QRmodal: {
        type:Boolean,
        default:false
      }
    },
    data() {
      return {
        pageData: [],
        qrids:'',
        param: {
          pageNum: 1,
          pageSize: 9999,
          jlJx: '',
          jlLx: '',
          yeGte: '1',
          orderBy: 'cjsj desc'
        },
        columns: [
          {title: '序号', type: 'index',align: 'center'},
          {
            type: 'selection',
            align: 'center',
            width:'50'
          },
          {
            title: '驾校',
            key: 'jlJx',
            align: 'center',

          },
          {
            title: '教练员',
            key: 'jlXm',
            align: 'center',

          },
          {
            title: '教练员电话',
            key: 'jlLxdh',
            align: 'center',

          },
          {
            title: '开放日余额',
            key: 'ye',
            align: 'center',
            render: (h, p) => {
              return h('Button', {
                props: {
                  type: 'error',
                  size: 'small',
                  ghost: true,
                },
                style: {},
                on: {
                  click: () => {
                    this.jlItem = p.row
                    this.lx = 'kfr'
                    this.isMxb = true
                  }
                }
              }, p.row.ye + '元')
            }
          }
        ]
      }
    },
    created(){
      this.getData()
    },
    methods: {
      getData() {
        this.$http.get('/api/lcwxjl/pager', {
          params: this.param
        }).then(res => {
          if (res.code == 200) {
            this.pageData = res.page.list
          } else {

          }
        }).catch(err => {
        })
      },
      ok() {
        if(this.qrids==''){
          this.$Message.error('请选择教练')
          return
        }

        var v=this
        this.swal({
          title: '是否确认清空所选教练的开放日余额？' ,
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '关闭',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            v.$http.post('/api/lcwxjl/updateZhye', {id:v.qrids}).then(res => {
              if (res.code == 200) {
                v.$emit('closeEmpty')
                v.getData()
                v.$Message.success(res.message)
              } else {
                v.$emit('toEmpty')
                v.$Message.error(res.message)
              }
            }).catch(err => {
            })
          }else{
          }
        })
      },
      cancel(){
        this.$emit('closeEmpty')
      },
      tabcheck(selection, row){
        let ids = []
        for (let r of selection) {
          ids.push(r.id)
        }
        let a = ids.join(',')
        this.qrids = a
        if (selection.length === 0) this.qrids = ''
      }
    }
  }
</script>

<style scoped>

</style>
