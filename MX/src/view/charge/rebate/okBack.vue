<template>
  <div>
    <Row style="padding: 12px 0" :gutter="12" type="flex" justify="end">
      <Col span="4">
      <DatePicker v-model="dateRange.jssj"
                  confirm format="yyyy-MM-dd"
                  @on-change="param.cjsjInRange = v.util.dateRangeChange(dateRange.jssj)"
                  @on-open-change="v.util.dateRangeChange(dateRange.jssj)"
                  type="daterange" :placeholder="'请输入返点时间'"  style="width: 200px"></DatePicker>
      </Col>
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
    <component :is="compName" :MSList="MSList"></component>
  </div>
</template>

<script>
  import fdms from './comp/fdms'

  export default {
    name: "okBack",
    components: {fdms},
    data() {
      return {
        v: this,
        compName: '',
        MSList:[],
        tableData: [],
        tableColumns: [
          {title: '#', type: 'index', fixed: 'left', align: 'center', minWidth: 80},
            {title: '教练员', key: 'jlXm', align: 'center', minWidth: 120},
          {
            title: '返点时间', key: 'cjsj', align: 'center', minWidth: 120, render: (h, p) => {
              let a = p.row.cjsj.substring(0, 16)
              return h('div', a)
            }
          },
          {title: '操作人', key: 'cjr', align: 'center', minWidth: 120},
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
                  }, '明细')
                ]
              )
            }
          }
        ],
        total: 0,
        totalS: 0,
        dateRange: {
          kssj: ''
        },
        param: {
            qrsjIsNotNull:'1',
            orderBy: 'qrsj desc',
          cjrLike: '',
          fdZt: '10',
          pageNum: 1,
          pageSize: 10,
          cjsjInRange:''
        }
      }
    },
    created() {
      this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
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
        this.$http.post('/api/fds/pager',this.param).then((res) => {
          if (res.code == 200 && res.page.list) {
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
