<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <!--<pager-tit title="教练员统计"></pager-tit>-->
    <div style="text-align: right;width: 100%">
      <Select v-model="param.lcKm" style="width: 100px;text-align: left" @on-change="v.util.initTable(v)">
        <Option v-for="item in kmList" :value="item.val"> {{ item.label }}</Option>
      </Select>
      <Select v-model="param.lcLx" style="width: 100px;text-align: left" @on-change="v.util.initTable(v)">
        <Option v-for="item in lcLxList" :value="item.val"> {{ item.label }}</Option>
      </Select>
      <DatePicker v-model="dateRange.kssj"
                  @on-change="param.kssjInRange = v.util.dateRangeChange(dateRange.kssj)" confirm format="yyyy-MM-dd"
                  type="daterange" :placeholder="'请输入'" style="width: 200px"
                  split-panels></DatePicker>
      <Button type="primary" @click="v.util.getPageData(v)" style="margin-left: 10px;">
        <Icon type="md-search"></Icon>
        <!--查询-->
      </Button>
      <!--<Button type="primary" @click="componentName = 'print'" style="margin-left: 10px;">-->
      <!--打印-->
      <!--</Button>-->
      <Button type="primary" @click="exportExcel" style="margin-left: 10px;">
        <Icon type="ios-cloud-download"/>
      </Button>
    </div>
    <!--<search-bar :parent="v" :show-create-button="false" :buttons="searchBarButtons" @print="componentName = 'print'" :show-search-button="false"></search-bar>-->
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="8" align="right">
        <div style="min-height: 1px"></div>
        <!--        <div style="font-size: 15px;font-weight: 600">-->
        <!--          时长合计：<span style="color: #ed3f14"> {{ zsc }} </span> 分钟-->
        <!--        </div>-->
      </Col>
      <Col span="8" align="right">
        <!--        <div style="font-size: 15px;font-weight: 600">-->
        <!--          应收合计：<span style="color: #ed3f14"> {{ zjes }} </span> 元-->
        <!--        </div>-->
        <div style="min-height: 1px"></div>
      </Col>
      <Col span="8" align="right">
        <div style="font-size: 15px;font-weight: 600">
          合计：<span style="color: #ed3f14"> {{ addmoney }} </span> 元
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

export default {
  name: 'char',
  components: {print},
  props: {
    lcKm: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      v: this,
      zjes: 0,
      zsc: 0,
      addmoney: 0,
      pagerUrl: this.apis.lcjl.jlTj,
      choosedItem: null,
      componentName: '',
      searchBarButtons: [
        {title: '打印', click: 'print'}
      ],
      dateRange: {
        kssj: ''
      },
      tableColumns: [
        {title: '序号', type: 'index'},
        {
          title: '驾校', key: 'jlJx',
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
        {
          title: '队号', render: (h, p) => {
            if (p.row.dm) {
              return h('div', p.row.dm)
            } else {
              return h('div', '-')
            }
          }
        },
        {title: '教练员', key: 'jlXm'},
        // {title: '时长', key: 'sc', minWidth: 80, defaul: '0'},
        // {title: '应收', key: 'zje'},
        {
          title: '练车金额', minWidth: 90, defaul: '0',
          render: (h, p) => {
            return h('div', p.row.zj + '元')
          }
        },
        {
          title: '返点金额',
          key: 'zfd'
        },
        {
          title: '已返点',
          key: 'yfd'
        },
        {
          title: '待返点',
          key: 'dfd'
        }
      ],
      pageData: [],
      pager: false,
      specialPageSize: 9999,
      lcLxList: [
        {val: '00', label: '计时'}, {val: '10', label: '按把'}, {val: '20', label: '培优'}, {val: '30', label: '开放'}
      ],
      kmList: [{val: '2', label: '科二'}, {val: '3', label: '科三'}],
      param: {
        total: 0,
        zhLike: '',
        lcKm: '',
        pageNum: 1,
        pageSize: 8,
        zfzt: '10',
        lcLx: ''
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

    this.param.lcKm = this.lcKm
    this.util.initTable(this);
  },
  methods: {
    exportExcel() {
      let p = '';
      for (let k in this.param) {
        p += '&' + k + '=' + this.param[k];
      }
      p = p.substr(1);
      window.open(this.apis.url + '/pub/jlExcel?' + p);
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
      this.zjes = 0;
      this.zsc = 0;
      this.addmoney = 0
      for (let r of list) {
        this.zjes += r.zje;
        this.zsc += r.sc;
        r.sc = this.parseTime(r.sc)
        this.addmoney += r.zj;
      }
      console.log(this.zsc)
    },
    getTime(s) {
      s = parseInt(s);
      let h = parseInt(s / 60);
      let m = s % 60;
      let r = '';
      if (h != 0) r += h + '小时'
      return r + m + '分钟'
    }
  }
}
</script>
