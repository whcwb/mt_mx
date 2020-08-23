<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <div style="display: flex;justify-content: space-between;align-items: center">
    <span
      style="cursor: pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
      @click="toPrint">收款凭证</span>
      <div style="margin-left: 70%">
        <Select v-model="param.jgdmLike" @on-change="v.util.initTable(v)">
          <Option v-for="item in JGList" :value="item.val">{{item.label}}</Option>
        </Select>
      </div>
      <search-bar :parent="v" :showSearchButton="true" :showDownLoadButton="true" :show-create-button="false"
                  :buttons="searchBarButtons" @print="componentName = 'print'"
                  @exportExcel="exportExcel"></search-bar>
    </div>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="24" align="right">
        <div style="font-size: 15px;font-weight: 600">
          实收合计：<span style="color: #ed3f14"> {{addmoney|GS}} </span> 元
        </div>
      </Col>
    </Row>
    <component :is="componentName" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  // import formData from './formModal.vue'
  import Cookies from 'js-cookie'
  import print from './print'
  import mixin from '@/mixins'
  //驾校统计
  import jxtj from '../jxtj'
  import printSignUp from './comp/printSignUp'

  export default {
    name: 'char',
    components: {print, jxtj,printSignUp},
    mixins:[mixin],
    data() {
      return {
        v: this,
        addmoney: 0,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        componentName: '',
        hisPrintMess: {},
        JGList: [{val: '100', label: '所有考场'}],
        searchBarButtons: [],
        dateRange: {
          kssj: ''
        },
        tableColumns: [
          {
            type: 'index2', minWidth: 60, align: 'center', title: '序号',
            render: (h, params) => {
              return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
            }
          },
          {
            title: '科目', align: 'center', minWidth: 80,
            render: (h, params) => {
              return h('div', params.row.lcKm === '2' ? '科目二' : '科目三');
            },
            filters: [
              {
                label: '科目二',
                value: '2'
              },
              {
                label: '科目三',
                value: '3'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self = this.$options.parent.parent
              _self.param.lcKm = value[0] ? value[0] : ''
              _self.util.getPageData(_self)
            },
          },
          {
            title: '驾校', align: 'center', key: 'jlJx', minWidth: 90,
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
              var _self = this.$options.parent.parent
              _self.param.lx = value[0] ? value[0] : ''
              _self.util.getPageData(_self)
            },
          },
          {title: '教练员', align: 'center', key: 'jlXm', minWidth: 80},
          {
            title: '车型', align: 'center', key: 'jlCx', minWidth: 80,
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
              var _self = this.$options.parent.parent
              if (value[0] === '0') {
                _self.param.jlCxIn = 'A,A1,A2,A3,B,B1,B2'
              } else if (value[0] === '1') {
                _self.param.jlCxIn = 'C,C1,C2'
              }
              else _self.param.jlCxIn = ''
              _self.util.getPageData(_self)
            },
          },
          {
            title: '类型', align: 'center', minWidth: 120,
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
                label: '开放日',
                value: 'KF'
              },
              {
                label: '按把',
                value: 'AB'
              },
            ],
            filterMultiple: false,
            filterRemote(value, row) {

              var _self = this.$options.parent.parent
              if (value[0]) {
                _self.param.zddmLike = value;
              }
              else _self.param.zddmLike = ''
              _self.util.getPageData(_self)
            },
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9 + ' ' + p.row.zdxm.zdmc)
              }

            }
          },
          {title: '人数', align: 'center', key: 'xySl', minWidth: 70},
          {title: '开始时间', align: 'center', key: 'kssj', searchType: 'daterange', minWidth: 135},
          {
            title: '结束时间', align: 'center', key: 'jssj', minWidth: 100,
            render: (h, p) => {
              return h('div', p.row.jssj.substring(10));
            }
          },
          {
            title: '时长', align: 'center', key: 'sc', minWidth: 100, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.sc);
            }
          },
          {
            title: '应收', align: 'center', minWidth: 70, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元');
            }
          },
          {
            title: '实收', align: 'center', minWidth: 70, defaul: '0',
            render: (h, p) => {
              if (p.row.zfzt == '00') {    //为已支付的，就显示现金
                return h('div', '');
              } else {
                return h('div', p.row.xjje + '元');
              }
            }
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
        ],
        pageData: [],
        specialPageSize: 9999,
        param: {
          orderBy: 'jssj desc',
          notShowLoading: 'true',
          total: 0,
          zhLike: '',
          pageNum: 1,
          pageSize: 8,
          zfzt: '10',
          lcKm: '',
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

          res.result.forEach((item, index) => {
            let t = {val: item.jgdm, label: item.jgmc}
            this.JGList.push(t)
          })
          this.param.jgdmLike = this.JGList[0].val
        })
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
        window.open(this.apis.url + '/api/lcjl/pagerExcelAll?token=' + token + '&userid=' + userid + "&" + p);
      },
      parseTime(s) {
        s = parseInt(s);
        let h = parseInt(s / 60);
        let m = s % 60;
        let r = '';
        if (h != 0) r += h + '小时'
        return r + m + '分钟'
      },
      afterPager(list) {
        this.addmoney = 0
        var v = this
        for (let r of list) {
          r.sc = this.parseTime(r.sc)
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)
          v.addmoney = v.addmoney + Number(r.xjje);
        }
      },
      print() {
        console.log('print');
      },
      toPrint(){
        let {
          param,
          hisPrintMess,
          addmoney
        }=this

        hisPrintMess.lcKm=param.lcKm
        hisPrintMess.addmoney=addmoney
        hisPrintMess.kssjInRange=param.kssjInRange

        if(hisPrintMess.lcKm=='') {
          this.$Message.error('请选择科目');
          return
        }

        if(hisPrintMess.addmoney===''||hisPrintMess.addmoney=='0') {
          this.$Message.error('当日无收款记录');
          return
        }

        this.hisPrintMess.name = JSON.parse(sessionStorage.getItem('userInfo')).xm
        this.componentName = 'printSignUp'
      }
    }
  }
</script>
