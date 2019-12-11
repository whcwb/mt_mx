<template>
  <div>
    <Row style="padding: 12px 0" :gutter="12" type="flex" justify="end">
      <!--<Col span="4">-->
      <DatePicker v-model="dateRange.cjsj"
                  confirm format="yyyy-MM-dd"
                  @on-change="param.cjsjInRange = v.util.dateRangeChange(dateRange.cjsj)"
                  @on-open-change="pageSizeChange(param.pageSize)"
                  type="daterange" :placeholder="'请输入返点时间'"  style="width: 200px"></DatePicker>
      <!--</Col>-->
        <Col span="4">
        <Input v-model="param.cjrLike" placeholder="操作人"
               @on-enter="getOldData()"/>
      </Col>
      <Col span="1" style="margin-right: 10px">
        <span>
          <Button type="primary" @click="getOldData">
            <Icon type="md-search"></Icon>
            <!--查询-->
          </Button>
        </span>
      </Col>
    </Row>
    <Table :height="AF.getPageHeight()-250" stripe size="small"
           :columns="tableColumns" :data="tableData"></Table>

    <Row style="display: flex;align-items: center;height: 36px;">
      <Col span="3" align="left">
        <i-switch v-model="switch1"></i-switch>
      </Col>
      <Col span="21" align="right" style="display: flex;justify-content: flex-end;">
          <span style="font-size: 12px;padding-top: 7px" v-if="switch1">
            <span style="font-size: 15px;font-weight: 600">笔数:&nbsp; </span>
          <span style="color: #ed3f14;font-size: 15px;">{{totalS }}</span>
            <span style="font-size: 15px;font-weight: 600"> &nbsp;笔&nbsp;&nbsp;</span>
          <span style="font-size: 15px;font-weight: 600">合计:&nbsp</span>
          <span style="color: #ed3f14;font-size: 15px;">{{hj|GS}}</span>
          <span style="font-size: 15px;font-weight: 600"> &nbsp;元&nbsp;&nbsp;</span>
       </span>
        <Page :total=totalS
              :current=param.pageNum
              :page-size=param.pageSize
              :page-size-opts=[8,10,15,20,30,40,50]
              show-total
              show-elevator
              show-sizer
              placement='top'
              @on-page-size-change='(n)=>{pageSizeChange(n)}'
              @on-change='(n)=>{pageChange(n)}'>
        </Page>
      </Col>
    </Row>
    <component :is="compName" :MSList="MSList"></component>
    <component :is="componentName"  :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  import fdms from './comp/fdms'
  import printSignUp from './comp/printSignUp'
  import Cookies from 'js-cookie'
  import mixin from '@/mixins'
  export default {
    name: "okBack",
    components: {fdms,printSignUp},
    mixins:[mixin],
    data() {
      return {
        v: this,
          hisPrintMess:{},
          componentName:'',
        compName: '',
        MSList:[],
        tableData: [],
        tableColumns: [
          {title: '序号', type: 'index', fixed: 'left', align: 'center', minWidth: 80},
          {title: '操作人', key: 'cjr', align: 'center', minWidth: 120},
          {title: '驾校', key: 'jx', align: 'center', minWidth: 120},
            {title: '教练员', key: 'jlXm', align: 'center', minWidth: 120},
          {
            title: '返点时间', key: 'cjsj', align: 'center', minWidth: 120, render: (h, p) => {
              let a = p.row.cjsj.substring(0, 16)
              return h('div', a)
            }
          },
          {title: '返点笔数', key: 'fdsl', align: 'center', minWidth: 120},
          {title: '返点金额', key: 'fdje', align: 'center', minWidth: 120},
          {
            title: '操作', minWidth: 120, align: 'center', render: (h, p) => {
              return h('Tooltip',
                {props: {placement: 'top', transfer: true, content: '明细',}},
                [
                  h('Button', {
                    props: {type: 'success', size: 'small',},
                    style: {marginRight: '10px'},
                    on: {
                      click: () => {
                        this.showMS(p.row.fds)
                      }
                    }
                  }, '明细'),
                    h('Button', {
                        props: {type: 'success', size: 'small',},
                        style: {marginRight: '10px'},
                        on: {
                            click: () => {
                                this.hisPrintMess = p.row
                                this.componentName = 'printSignUp'
                            }
                        }
                    }, '打印')
                ]

              )
            }
          }
        ],
        total: 0,
        totalS: 0,
        dateRange: {
          cjsj: ''
        },
        hj:0,
        switch1: true,
        param: {
            qrsjIsNotNull:'1',
            orderBy: 'qrsj desc',
          cjrLike: '',
          fdZt: '10',
          pageNum: 1,
          pageSize: 15,
          cjsjInRange:''
        }
      }
    },
    watch: {
      switch1:function (val) {
        Cookies.set('showMessFD',val)
      }
    },
    mounted() {
      this.switch1=Cookies.get('showMessFD')==='true'?true:false
    },
    created() {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      this.dateRange.cjsj = [start, end]
      var d = start;
      var c = end;
      var datetimed = this.AF.trimDate(start) + ' ' + '00:00:00';
      var datetimec = this.AF.trimDate() + ' 23:59:59';
      this.param.cjsjInRange = datetimed + ',' + datetimec
      // this.dateRange.cjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      // this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59';
      this.getOldData()
    },
    methods: {
      pageChange(val) {
        this.param.pageNum = val
        this.getOldData();
      },
      pageSizeChange(val) {
        this.param.pageSize = val
        this.getOldData();
      },
      showMS(list){
        this.MSList = list
        console.log(list)
        this.compName = fdms
      },
      getOldData() {
        this.hj= 0
        this.$http.post('/api/fds/getPager',this.param).then((res) => {
          if (res.code == 200 && res.page.list) {
            this.hj = res.result
            this.totalS = res.page.total
            this.tableData = res.page.list;
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
