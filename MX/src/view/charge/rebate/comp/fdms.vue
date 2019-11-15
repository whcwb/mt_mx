<template>
  <div>
    <Modal
      v-model="modalShow"
      width="1200"
      :closable='false'
      :footer-hide="true"
      class-name="vertical-center-modal"
      :mask-closable="false">
      <div slot="header">
        <Row>
          <Col span="12">
            <h2>返点明细</h2>
          </Col>
          <Col span="12" align="right">
              <Button @click="close">关闭</Button>
          </Col>
        </Row>
      </div>
      <div>
        <Table :height="AF.getPageHeight()-350" stripe size="small"
               :columns="tableColumns" :data="MSList"></Table>
      </div>
    </Modal>
  </div>
</template>

<script>
  export default {
    name: "fdms",
    props: {
      MSList: {
        type: Array,
        default: []
      }
    },
    data() {
      return {
        modalShow: true,
        tableColumns: [
          {title: '序号', align: 'center', type: 'index', fixed: 'left', minWidth: 80},
          {title: '凭证号', align: 'center', key: 'id', minWidth: 150},
          {title: '教练员', align: 'center', key: 'jlXm', minWidth: 150},
          {title: '返点时间', key: 'cjsj', align: 'center', minWidth: 120},
          {title: '返点金额', key: 'fdje', dict: 'km', align: 'center', minWidth: 120},
         ],
      }
    },
    methods:{
      close(){
        this.$parent.compName = ''
      }
    }
  }
</script>

<style lang="less">

</style>
