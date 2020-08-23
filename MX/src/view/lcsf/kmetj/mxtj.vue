<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <Row>
      <Col span="17">
        <div style="width: 100%;min-height:1px;"></div>
      </Col>
      <Col span="3" style="padding-right: 60px">
        <Select v-if="JGList.length > 1" v-model="param.jgdmLike" @on-change="getPageData">
          <Option v-for="item in JGList" :value="item.val">{{item.label}}</Option>
        </Select>
        <div v-else style="min-height: 1px"></div>
      </Col>
      <Col span="4">
        <search-bar :parent="v" :showSearchButton="true" :showDownLoadButton="true" :show-create-button="false"
                    :buttons="searchBarButtons" @print="componentName = 'print'"
                    @exportExcel="exportExcel">
        </search-bar>
      </Col>
    </Row>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
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
  import Cookies from 'js-cookie'
  import print from './print'
  //驾校统计
  import jxtj from '../jxtj'
  import mixin from '@/mixins'


  export default {
    name: 'char',
    mixins: [mixin],
    components: {print, jxtj},
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
          {
            type: 'index2', minWidth: 60, align: 'center', title: '序号',
            render: (h, params) => {
              return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
            }
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
          {title: '开始时间', align: 'center', key: 'kssj', searchType: 'daterange', minWidth: 135},
          {title: '结束时间', align: 'center', key: 'jssj', minWidth: 135},
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
            },
            filters: [
              {
                label: '现金',
                value: '1',
              },
              {
                label: '开放日',
                value: '2'
              },
              {
                label: '充值卡',
                value: '3'
              }],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self = this.$options.parent.parent
              console.log(value)
              console.log(_self.param);
              _self.param.zflx = value[0]
              _self.util.initTable(_self)
            }
          },
          {
            title: '类型', align: 'center', minWidth: 100,
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9)
              }

            }
          },
        ],
        pageData: [],
        specialPageSize: 9999,
        param: {
          orderBy: 'jssj desc',
          notShowLoading: 'true',
          total: 0,
          lcKm: '2',
          zhLike: '',
          pageNum: 1,
          pageSize: 8,
          zfzt: '10',
          zflx: '',
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
      getPageData() {
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
        window.open(this.apis.url + '/api/lcjl/pagerExcel?token=' + token + "&userid=" + userid + "&" + p);
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
    }
  }
</script>
