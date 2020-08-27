<template>
  <div>
    <Row style="padding: 12px 0" :gutter="12" type="flex" justify="end">
      <Select v-if="JGList.length > 1" v-model="param.jgdmLike" style="width: 100px;margin-right: 10px"
              @on-change="getOldData" placeholder="请选择考场">
        <Option v-for="item in JGList" :value="item.val">{{ item.label }}</Option>
      </Select>
      <DatePicker v-model="dateRange.cjsj"
                  confirm format="yyyy-MM-dd"
                  @on-change="param.cjsjInRange = v.util.dateRangeChange(dateRange.cjsj)"
                  @on-open-change="pageSizeChange(param.pageSize)"
                  type="daterange" :placeholder="'请输入返点时间'" style="width: 200px"></DatePicker>
      <!--</Col>-->
      <Col span="3">
        <Select v-model="param.lcKm" placeholder="练车科目" @on-change="getOldData" clearable>
          <Option value="2">科目二</Option>
          <Option value="3">科目三</Option>
        </Select>
      </Col>
      <Col span="3">
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
      <Col span="1" style="margin-right: 10px">
        <span>
          <Tooltip content="下载返现登记表" placement="top">
          <Button type="primary" @click="downLoadExcel">
            <Icon type="ios-cloud-download"></Icon>
            <!--查询-->
          </Button>
            </Tooltip>
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
    <component :is="componentName" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
import fdms from './comp/fdms'
import printSignUp from './comp/printSignUp'
import Cookies from 'js-cookie'
import mixin from '@/mixins'

export default {
  name: "okBack",
  components: {fdms, printSignUp},
  mixins: [mixin],
  props: {
    JGList: {
      type: Array,
      default: []
    }
  },
  data() {
    return {
      v: this,
      hisPrintMess: {},
      componentName: '',
      JGList: [],
      compName: '',
      MSList: [],
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
        hj: 0,
        switch1: true,
        param: {
          qrsjIsNotNull: '1',
          orderBy: 'qrsj desc',
          cjrLike: '',
          fdZt: '10',
          pageNum: 1,
          pageSize: 15,
          cjsjInRange: '',
          lcKm: '',
          jgdmLike: ''
        }
      }
    },
    watch: {
      switch1: function (val) {
        Cookies.set('showMessFD', val)
      }
    },
    mounted() {
      this.switch1 = Cookies.get('showMessFD') === 'true'
    },
    created() {
      if (this.JGList.length > 1) {
        this.param.jgdmLike = this.JGList[0].val
      }
      this.dateRange.cjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59';
      this.getOldData()
    },
    methods: {
      downLoadExcel() {
        if (this.param.lcKm == undefined || this.param.lcKm == "") {
          this.swal({
            title: '导出文件时请选择科目',
            type: 'warning',
          })
          return
        }

        let accessToken = JSON.parse(Cookies.get('accessToken'));
        let token = accessToken.token;
        let userid = accessToken.userId;
        window.open(this.apis.url + "/api/fds/fdExcel?token=" + token + "&userid=" + userid + "&qrsjIsNotNull=1&orderBy=qrsj desc&cjrLike=" + this.param.cjrLike + "&fdZt=10&" +
          "cjsjInRange=" + this.param.cjsjInRange + "&lcKm=" + this.param.lcKm)

      },
      pageChange(val) {
        this.param.pageNum = val
        this.getOldData();
      },
      pageSizeChange(val) {
        this.param.pageSize = val
        this.getOldData();
      },
      showMS(list) {
        this.MSList = list
        console.log(list)
        this.compName = fdms
      },
      getOldData() {
        this.hj = 0
        this.$http.post('/api/fds/getPager', this.param).then((res) => {
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
