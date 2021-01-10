<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <div style="display: flex;justify-content: space-between;align-items: center">
    <span
      style="cursor: pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
      @click="toPrint">收款凭证</span>
      <search-bar :parent="v" :showSearchButton="true" :showDownLoadButton="true" :show-create-button="false"
                  :buttons="searchBarButtons" @print="componentName = 'print'"
                  @exportExcel="exportExcel" style="position: absolute;right: 0; margin-right: 80px"></search-bar>
      <Select style="width: 150px;position: absolute;margin-left: 57.5%" clearable @on-change="getPageData" v-model="param.orgcode"  placeholder="请选择队号">
        <Option v-for="item in dhs" :value="item.dh">
          {{item.dm}}
        </Option>
      </Select>
      <Tooltip content="本校明细下载" placement="top" >
        <Button type="success" style="margin-right: 20px" @click="downLoadLocalSchool" icon="ios-cloud-download"></Button>
      </Tooltip>
    </div>

    <Table :columns="tableColumns" :height="40"></Table>
    <div>
      <virtual-list style="height: 660px; overflow-y: auto;"
                    :data-key="'id'"
                    :data-sources="pageData"
                    :data-component="itemComponent"
                    @child-event="parentevent"
      />
    </div>
    <!--    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>-->
    <Row>
      <Col span="24" align="right">
        <div style="font-size: 15px;font-weight: 600">
          实收合计：<span style="color: #ed3f14"> {{ addmoney|GS }} </span> 元
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
import virtualList from 'vue-virtual-scroll-list'
import Item from './comp/Item'
import dhList from '../../../data/dhList'

export default {
  name: 'char',
  components: {print, jxtj, printSignUp, virtualList},
  mixins: [mixin],
  data() {
    return {
      v: this,
      itemComponent: Item,
      dhs:dhList.dhs,
      addmoney: 0,
      apiRoot: this.apis.lcjl,
      choosedItem: null,
      componentName: '',
      hisPrintMess: {},
      searchBarButtons: [
        // {title: '打印', click: 'print'},
        //   {title: '导出', click: 'exportExcel'}
      ],
      dateRange: {
        kssj: ''
      },
      tableData: [],
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
            var _self = this
            _self.param.lcKm = value[0] ? value[0] : ''
            _self.getPageData()
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
            var _self = this
            _self.param.lx = value[0] ? value[0] : ''
            _self.getPageData()
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
            var _self = this
            if (value[0] === '0') {
              _self.param.jlCxIn = 'A,A1,A2,A3,B,B1,B2'
            } else if (value[0] === '1') {
              _self.param.jlCxIn = 'C,C1,C2'
            } else _self.param.jlCxIn = ''
            _self.getPageData()
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
            var _self = this
            console.log("aa", value[0])
            if (value[0]) {
              _self.param.zddmLike = value[0];
            } else {
              _self.param.zddmLike = ''
            }
            _self.getPageData();
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
      specialPageSize: 99999,
      param: {
        orderBy: 'jssj desc',
        notShowLoading: 'true',
        total: 0,
        zhLike: '',
        pageNum: 1,
        pageSize: 99999,
        zfzt: '10',
        lcKm: '',
        orgcode:''
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
    this.getPageData();
  },
  methods: {
    downLoadLocalSchool(){
      let p = '';
      for (let k in this.param) {
        p += '&' + k + '=' + this.param[k];
      }
      p = p.substr(1);
      let accessToken = JSON.parse(Cookies.get('accessToken'));
      let token = accessToken.token;
      let userid = accessToken.userId;
      window.open(this.apis.url + '/api/lcjl/downloadLocalSchool?token=' + token + "&userid=" + userid + "&" + p);
    },
    getPageData() {
      this.$http.get('/api/lcjl/pager', {params: this.param}).then(res => {
        if (res.code == 200) {
          this.pageData = res.page.list
          this.addmoney = 0
          var v = this
          for (let r of this.pageData) {
            r.sc = this.parseTime(r.sc)
            r.kssj = r.kssj.substring(0, 16)
            r.jssj = r.jssj.substring(0, 16)
            v.addmoney = v.addmoney + Number(r.xjje);
          }
        }
      })
    },
    parentevent(val) {
      console.log(val, 'val');
    },
    exportExcel() {
      let p = '';
      for (let k in this.param) {
        p += '&' + k + '=' + this.param[k];
      }
      p = p.substr(1);
      window.open(this.apis.url + '/pub/pagerExcelAll?' + p);
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
    toPrint() {
      let {
        param,
        hisPrintMess,
        addmoney
      } = this

      hisPrintMess.lcKm = param.lcKm
      hisPrintMess.addmoney = addmoney
      hisPrintMess.kssjInRange = param.kssjInRange

      if (hisPrintMess.lcKm == '') {
        this.$Message.error('请选择科目');
        return
      }

      if (hisPrintMess.addmoney === '' || hisPrintMess.addmoney == '0') {
        this.$Message.error('当日无收款记录');
        return
      }

      this.hisPrintMess.name = JSON.parse(sessionStorage.getItem('userInfo')).xm
      this.componentName = 'printSignUp'
    }
  }
}
</script>
