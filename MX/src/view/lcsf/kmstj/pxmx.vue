<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
      <search-bar :parent="v" :show-create-button="false" :buttons="searchBarButtons"
                  @print="componentName = 'print'"
                  @exportExcel="exportExcel"
      ></search-bar>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-230" :pager="false"></table-area>
    <Row>
        <!--<Col span="1" align="center" >-->
          <!--<div @click="AnYearTJ" style="padding: 10px;border-radius: 35px;background-color: #10AEFF;color: white;font-size: 28px">年</div>-->
        <!--</Col>-->
        <!--<Col span="1" align="center">-->
          <!--<div @click="AnJdTJ" style="padding: 10px;border-radius: 35px;background-color: #10AEFF;color: white;font-size: 28px">季</div>-->
        <!--</Col>-->
        <!--<Col span="1" align="center">-->
          <!--<div @click="AnMonTJ" style="padding: 10px;border-radius: 35px;background-color: #10AEFF;color: white;font-size: 28px">月</div>-->
        <!--</Col>-->
        <!--<Col span="1" align="center">-->
          <!--<div @click="AnWeekTJ" style="padding: 10px;border-radius: 35px;background-color: #10AEFF;color: white;font-size: 28px">周</div>-->
        <!--</Col>-->
      <Col span="24" align="right">
        <div style="font-size: 15px;font-weight: 600">
          实收合计：<span style="color: #ed3f14"> {{addmoney}} </span> 元
        </div>
      </Col>
    </Row>
    <component :is="componentName"></component>
  </div>
</template>

<script>
  // import formData from './formModal.vue'
  import print from './print'
  import moment from 'moment'
  import Cookies from 'js-cookie'

  export default {
    name: 'char',
    components: {print},
    data() {
      return {
        v: this,
        addmoney:0,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        componentName: '',
        searchBarButtons: [
          // {title: '打印', click: 'print'},
          // {title: '导出', click: 'exportExcel'}
        ],
        dateRange: {
          kssj: ''
        },
        tableColumns: [
          {type: 'index', align: 'center', minWidth: 50, title: '序号'},
          {title: '驾校', key: 'jlJx', minWidth: 60, align: 'center',},
          {title: '教练员', key: 'jlXm', minWidth: 50, align: 'center',},
          {title: '车号', key: 'clBh', minWidth: 50, align: 'center',},
          {
            title: '人数',
            key: 'xySl',
            minWidth: 50,
            align: 'center',
            render: (h, p) => {
              if (p.row.xySl != '' && p.row.xySl != undefined) {
                return h('div', p.row.xySl + '人')
              } else {
                return ''
              }

            }
          },
          {title: '车型', key: 'jlCx', minWidth: 50, align: 'center',
          },
          {
            title: '类型',
            minWidth: 70,
            align: 'center',
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9 + ' ' + p.row.zdxm.zdmc)
              }

            },
          },
          {title: '训练时间', key: 'kssj', searchType: 'daterange',minWidth: 60, align: 'center',
            render: (h, p) => {
                return h('div', p.row.kssj.substring(0,10));
            }
          },
          {
            title: '实收',minWidth: 60, defaul: '0', align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00') {    //为已支付的，就显示现金
                return h('div', '');
              }else{
                return h('div', p.row.xjje + '元');
              }
            }
          },
          {
            title: '安全员',
            minWidth: 50,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.zgXm)
            }
          },
          {
            title: '备注',
            minWidth: 250,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.bz)
            }
          }
        ],
        pageData: [],
        specialPageSize:99999999,
        param: {
          notShowLoading:'true',
          orderBy: 'jssj desc',
          total: 0,
          lcKm: '3',
          zhLike: '',
        },
      }
    },
    created(){
      if(Cookies.get("daterange")!=undefined&&Cookies.get("daterange")!=''){
        this.dateRange.kssj = Cookies.get("daterange").split(',')
        this.param.kssjInRange = Cookies.get("daterange")
      }else {
        this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
        this.param.kssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      }

     this.util.initTable(this);
    },
    methods: {
        AnYearTJ(){
          let startTime = moment().subtract(1, 'year').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          this.param.kssjInRange = startTime + ',' + endTime
          this.util.initTable(this);
        },
        AnJdTJ(){
          let startTime = moment().subtract(3, 'months').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          this.param.kssjInRange = startTime + ',' + endTime
          this.util.initTable(this);
        },
        AnMonTJ(){
          let startTime = moment().subtract(1, 'months').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          this.param.kssjInRange = startTime + ',' + endTime
          this.util.initTable(this);
        },
        AnWeekTJ(){
          let startTime = moment().subtract(7, 'day').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
          this.param.kssjInRange = startTime + ',' + endTime
          this.util.initTable(this);
        },
        exportExcel(){
            let p = '';
            for (let k in this.param){
                p += '&'+k + '=' +this.param[k];
            }
            p = p.substr(1);
            window.open(this.apis.url + '/pub/pagerExcelK3?'+p);
        },
      afterPager(list){
        let arr1=[]
        this.pageData.map((val,index,arr)=>{
          if(!val.jlCx.includes('C'))
            arr1.push(val)
        })
        this.pageData=list=arr1

        this.addmoney = 0
        for (let r of list){
            r.kssj = r.kssj.substring(0,16)
            r.jssj = r.jssj.substring(0,16)
          this.addmoney += Number(r.xjje);
        }
      },
      print() {
        console.log('print');
      }
    }
  }
</script>
