<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <Row>
      <Col span="17">
        <div style="width: 100%;min-height:1px;"></div>
      </Col>
      <Col span="3" style="padding-right: 60px">
        <Select v-if="JGList.length > 1" v-model="param.jgdmLike" @on-change="v.util.initTable(v)">
          <Option v-for="item in JGList" :value="item.val">{{item.label}}</Option>
        </Select>
        <div v-else style="min-height: 1px"></div>
      </Col>
      <Col span="4">
        <search-bar :parent="v" :show-create-button="false" :buttons="searchBarButtons"
                    @print="componentName = 'print'"
                    @exportExcel="exportExcel"
        ></search-bar>
      </Col>
    </Row>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-230" :pager="false"></table-area>
    <Row>
      <Col span="24" align="right">
        <div style="font-size: 15px;font-weight: 600">
          实收合计：<span style="color: #ed3f14"> {{addmoney | GS}} </span> 元
        </div>
      </Col>
    </Row>
    <component :is="componentName"></component>
  </div>
</template>

<script>
  import print from './print'
  import moment from 'moment'
  import Cookies from 'js-cookie'
  import mixin from '@/mixins'

  export default {
    name: 'char',
    components: {print},
    mixins: [mixin],
    data() {
      return {
        v: this,
        addmoney: 0,
        apiRoot: this.apis.lcjl,
        JGList: [{val: '100', label: '所有考场'}],
        choosedItem: null,
        componentName: '',
        searchBarButtons: [],
        dateRange: {
          kssj: ''
        },
        tableColumns: [
          {type: 'index', align: 'center', minWidth: 60, title: '序号', fixed: 'left'},
          {
            title: '驾校', key: 'jlJx', minWidth: 90, align: 'center',
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
          {title: '教练员', key: 'jlXm', minWidth: 90, align: 'center',},
          {title: '车号', key: 'clBh', minWidth: 60, align: 'center',},
          {
            title: '车型', key: 'jlCx', minWidth: 90, align: 'center',
            filters: [
              {
                label: '大车',
                value: '0'
              },
              {
                label: '小车',
                value: '1'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self =  this.$options.parent.parent
              if(value[0]==='0'){
                _self.param.jlCxIn='A,A1,A2,A3,B,B1,B2'
              }else if(value[0]==='1'){
                _self.param.jlCxIn='C,C1,C2'
              }
              else _self.param.jlCxIn=''
              _self.util.getPageData(_self)
            },
          },
          {
            title: '类型',
            minWidth: 140,
            align: 'center',
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9 + ' ' + p.row.zdxm.zdmc)
              }

            },
            filters: [
              {
                label: '计时',
                value: 'JS'
              },
              {
                label: '培优',
                value: 'PY'
              },
              {
                label: '按把',
                value: 'AB'
              },
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self =  this.$options.parent.parent
              _self.param.zddmLike = value;
              _self.util.getPageData(_self)
            }
          },
          {
            title: '人数',
            key: 'xySl',
            minWidth: 80,
            align: 'center',
            render: (h, p) => {
              if (p.row.xySl != '' && p.row.xySl != undefined) {
                return h('div', p.row.xySl + '人')
              } else {
                return ''
              }

            }
          },
          {title: '开始时间', key: 'kssj', searchType: 'daterange', minWidth: 140, align: 'center',},
          {
            title: '结束时间', key: 'jssj', minWidth: 90, align: 'center',
            render: (h, p) => {
              return h('div', p.row.jssj.substring(10, 16))
            }
          },
          {
            title: '时长', key: 'sc', minWidth: 80, defaul: '0', align: 'center',
            render: (h, p) => {
              return h('div', p.row.sc + '分钟')
            }
          },
          {
            title: '实收', minWidth: 90, defaul: '0', align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00') {    //为已支付的，就显示现金
                return h('div', '');
              } else {
                return h('div', p.row.xjje + '元');
              }
            }
          },
          {
            title: '订单状态', key: 'zfzt', minWidth: 100, align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00') {
                return h('div',
                  [
                    h('Button', {
                      props: {
                        type: 'error',
                        size: 'small',
                        ghost: true,
                      },
                      style: {},
                    }, '未支付')
                  ])

                return h('div', '未支付')
              } else {
                return h('div', '已支付')
              }
            },
          },
          {
            title: '支付方式', align: 'center', minWidth: 100, defaul: '0',
            render: (h, p) => {
              if (p.row.zfzt == '00') {
                return h('div', '');
              } else {
                return h('div', p.row.zffs);
              }
            }
          },
          {
            title: '安全员',
            minWidth: 100,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.zgXm)
            }
          },
          {
            title: '备注',
            width: 250,
            align: 'center',
            render: (h, p) => {
              return h('div', {
                style: {
                  textAlign: 'left',
                  whiteSpace: "pre-wrap"
                }
              }, p.row.bz)
            }
          }
        ],
        pageData: [],
        specialPageSize: 99999999,
        param: {
          notShowLoading: 'true',
          orderBy: 'jssj desc',
          total: 0,
          lcKm: '3',
          zhLike: '',
          zfzt: '10',
          jlCxIn: '',
          jgdmLike: '100'
        },
      }
    },
    created() {
      if (Cookies.get("daterange") != undefined && Cookies.get("daterange") != '') {
        this.dateRange.kssj = Cookies.get("daterange").split(',')
        this.param.kssjInRange = Cookies.get("daterange")
      } else {
        this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
        this.param.kssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      }
      this.getJgsByOrgCode();
      this.util.initTable(this);
    },
    methods: {
      getJgsByOrgCode() {
        this.$http.get("/api/lccl/getJgsByOrgCode").then(res => {
          if (res.result.length <= 1) {
            this.JGList = []
          }
          for (let r of res.result) {
            let t = {val: r.jgdm, label: r.jgmc}
            this.JGList.push(t)
          }
          this.param.jgdmLike = this.JGList[0].val
        })
      },
      AnYearTJ() {
        let startTime = moment().subtract(1, 'year').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        this.param.kssjInRange = startTime + ',' + endTime
        this.util.initTable(this);
      },
      AnJdTJ() {
        let startTime = moment().subtract(3, 'months').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        this.param.kssjInRange = startTime + ',' + endTime
        this.util.initTable(this);
      },
      AnMonTJ() {
        let startTime = moment().subtract(1, 'months').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        this.param.kssjInRange = startTime + ',' + endTime
        this.util.initTable(this);
      },
      AnWeekTJ() {
        let startTime = moment().subtract(7, 'day').format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        let endTime = moment().format('YYYY[-]MM[-]DD HH[:]mm[:]ss')
        this.param.kssjInRange = startTime + ',' + endTime
        this.util.initTable(this);
      },
      exportExcel() {
        let p = '';
        for (let k in this.param) {
          p += '&' + k + '=' + this.param[k];
        }
        p = p.substr(1);
        let accessToken = JSON.parse(Cookies.get('accessToken'));
        let token = accessToken.token;
        let userid = accessToken.userId;
        window.open(this.apis.url + '/api/lcjl/pagerExcelK3?token=' + token + '&userid=' + userid + '&' + p);
      },
      afterPager(list) {
        let arr1 = []
        this.pageData.map((val, index, arr) => {
          if (val.jssj !== '') {
            arr1.push(val)
          }
        })
        this.pageData = list = arr1

        this.addmoney = 0
        for (let r of list) {
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)
          this.addmoney += Number(r.xjje);
        }
      },
      print() {
        console.log('print');
      }
    }
  }
</script>
