<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <search-bar :parent="v" :showSearchButton="true" :showDownLoadButton="true" :show-create-button="false" :buttons="searchBarButtons" @print="componentName = 'print'"
                @exportExcel="exportExcel"></search-bar>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
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
  import Cookies from 'js-cookie'
  import print from './print'
  //驾校统计
  import jxtj from  '../jxtj'

  export default {
    name: 'char',
    components: {print,jxtj},
    data() {
      return {
        v: this,
        addmoney:0,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        componentName: '',
        searchBarButtons: [
          // {title: '打印', click: 'print'},
          //   {title: '导出', click: 'exportExcel'}
        ],
        dateRange: {
          kssj: ''
        },
        // tableColumns: [
        //   {
        //     type: 'index2', align: 'center', minWidth: 60,title: '序号',
        //     render: (h, params) => {
        //       return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
        //     }
        //   },
        //   {
        //     title: '车号', key: 'clBh', align: 'center',minWidth: 60, render: (h, p) => {
        //       return h('Tag', {
        //         props: {
        //           type: 'volcano',
        //         }
        //       }, p.row.clBh)
        //     }
        //   },
        //   {title: '驾校', key: 'jlJx', minWidth: 90},
        //   {title: '教练员', key: 'jlXm',  minWidth: 90},
        //   {title: '学员数量', key: 'xySl', minWidth: 70, defaul: '0'},
        //   {title: '开始时间', key: 'kssj', searchType: 'daterange', minWidth: 150},
        //   {title: '结束时间', key: 'jssj', minWidth: 150},
        //   {title: '时长', key: 'sc', minWidth: 80, default: '0'},
        //   // {title: '计费类型', key: 'lcLx', minWidth: 90, dict: 'ZDCLK1048'},
        //   {title: '费用', key: 'lcFy', append: '元', minWidth: 90, defaul: '0'},
        //   // {title:'操作',render:(h,p)=>{
        //   //     let buttons = [];
        //   //     buttons.push(this.util.buildeditButton(this,h,p));
        //   //     buttons.push(this.util.buildDeleteButton(this,h,p.row.yhid));
        //   //     return h('div',buttons);
        //   //   }
        //   //   },
        //
        // ],
        tableColumns: [
          {
            type: 'index2', minWidth: 60, align: 'center', title: '序号',
            render: (h, params) => {
              return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
            }
          },
          {title: '驾校', align: 'center', key: 'jlJx', minWidth: 90,
            filters: [
              {
                label: '本校',
                value: '00'
              },
              {
                label: '外校',
                value: '10'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self =  this.$options.parent.parent
              _self.param.lx=value[0]?value[0]:''
              _self.util.getPageData(_self)
            },
          },
          {title: '教练员', align: 'center', key: 'jlXm', minWidth: 80},
          {title: '开始时间', align: 'center', key: 'kssj',searchType: 'daterange', minWidth: 135},
          {title: '结束时间', align: 'center', key: 'jssj', minWidth: 135},
          // {
          //   title: '时长', align: 'center', key: 'sc', minWidth: 70, defaul: '0',
          //   render: (h, p) => {
          //     return h('div', p.row.sc);
          //   }
          // },
          // {
          //   title: '应收', align: 'center', minWidth: 70, defaul: '0',
          //   render: (h, p) => {
          //     return h('div', p.row.lcFy + '元');
          //   }
          // },
          {
            title: '实收', align: 'center', minWidth: 70, defaul: '0',
            render: (h, p) => {
              if (p.row.zfzt == '00') {    //为已支付的，就显示现金
                return h('div', '');
              }else{
                return h('div', p.row.xjje + '元');
              }
            }
          },
          {
            title: '支付方式', align: 'center', minWidth: 100, defaul: '0',
            render: (h, p) => {
              if (p.row.zfzt == '00') {
                return h('div', '');
              }else{
                return h('div', p.row.zffs);
              }
            }
          },
          {
            title: '类型', align: 'center', minWidth: 100,
            // filters: [
            //   {
            //     label: '计时',
            //     value: 'JS'
            //   },
            //   {
            //     label: '培优',
            //     value: 'PY'
            //   },
            //   {
            //     label: '开放日',
            //     value: 'KF'
            //   },
            // ],
            // filterMultiple: false,
            // filterRemote(value, row) {
            //   this.param.zddmLike = value;
            //   this.util.getPageData(this);
            // },
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9)
              }

            }
          },
        ],
        pageData: [],
        specialPageSize:9999,
        param: {
          orderBy: 'jssj desc',
            notShowLoading:'true',
          total: 0,
          lcKm: '2',
          zhLike: '',
          pageNum: 1,
          pageSize: 8,
          zfzt:'10'
        },
      }
    },
    created() {
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
        exportExcel(){
            let p = '';
            for (let k in this.param){
                p += '&'+k + '=' +this.param[k];
            }
            p = p.substr(1);
            window.open(this.apis.url + '/pub/pagerExcel?'+p);
        },
      parseTime(s) {
        s = parseInt(s);
        let h = parseInt(s / 60);
        let m = s % 60;
        let r = '';
        if (h != 0) r += h + '小时'
        return r + m + '分钟'
      },
      afterPager(list){
        this.addmoney = 0
          var v = this
        for (let r of list){
            r.sc = this.parseTime(r.sc)
            r.kssj = r.kssj.substring(0,16)
            r.jssj = r.jssj.substring(0,16)
            v.addmoney = v.addmoney + Number(r.xjje);
        }
      },
      print() {
        console.log('print');
      },
    }
  }
</script>
