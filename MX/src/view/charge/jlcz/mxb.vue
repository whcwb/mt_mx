<template>
  <Modal
    v-model="isMxb"
    :title="itemObj.jlXm+' 明细表'"
    :closable='false'
    @on-ok="ok">
    <Table stripe
           :height="AF.getPageHeight()-250"
           :columns="columns2"
           :data="dataList">
    </Table>
    <div slot="footer">
      <Button @click="cancel">
        取消
      </Button>
    </div>
  </Modal>
</template>

<script>
  export default {
    name: "mxb",
    props: {
      itemObj:{
        type:Object,
        default:{}
      },
      isMxb:{
        type:Boolean,
        default:false
      }
    },
    data(){
      return{
        mx:false,
        dataList:[],
        columns2: [
          {title: '#', type: 'index', width: 60},
          {
            title: '卡号',
            key: 'cardNo',
            align: 'center',

          },
          {
            title: '驾校',
            key: 'jlJx',
            align: 'center',

          },
          {
            title: '教练姓名',
            key: 'jlXm',
            align: 'center',

          },
          {
            title: '联系电话',
            key: 'jlLxdh',
            align: 'center',

          },
          {
            title: '余额',
            key: 'jlLxdh',
            align: 'center',

          }
        ],
      }
    },
    created(){

    },
    methods:{
      ok(){

      },
      cancel(){
        this.$emit('closemxb')
      }
    }
  }
</script>

<style scoped>
  /deep/ .ivu-modal{
    width: 70%!important;
  }
</style>
