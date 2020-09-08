<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <!--<pager-tit title="教练员统计"></pager-tit>-->
    <div style="text-align: right;width: 100%">
      <div v-if="lcKm" style="width: 100px;text-align: left; margin-right: 5px"></div>
      <Select v-else v-model="param.lcKm" style="width: 100px;text-align: left; margin-right: 5px" clearable
              @on-change="v.util.initTable(v)" placeholder="科目">
        <Option v-for="item in kmList" :value="item.val"> {{ item.label }}</Option>
      </Select>
      <Select v-if="JGList.length > 1" v-model="param.jgdmLike" style="width: 100px;text-align: left; margin-right: 5px"
              @on-change="v.util.initTable(v)">
        <Option v-for="item in JGList" :value="item.val">{{ item.label }}</Option>
      </Select>
      <Select v-model="param.lcLx" style="width: 100px;text-align: left;margin-right: 5px"
              @on-change="v.util.initTable(v)" placeholder="计费类型">
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
      <!--打印-->
      <Button type="primary" @click="exportExcel" style="margin-left: 10px;">
        <Icon type="ios-cloud-download"/>
      </Button>
    </div>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="6" align="right">
        <div style="font-size: 15px;font-weight: 600">
          人数合计：<span style="color: #ed3f14"> {{ zrs }} </span> 人
        </div>
      </Col>
      <Col span="6" align="right">
        <div style="font-size: 15px;font-weight: 600">
          时长合计：<span style="color: #ed3f14"> {{ zsc }} </span> 分钟
        </div>
      </Col>
      <Col span="6" align="right">
        <div style="font-size: 15px;font-weight: 600">
          应收合计：<span style="color: #ed3f14"> {{ zjes }} </span> 元
        </div>
      </Col>
      <Col span="6" align="right">
        <div style="font-size: 15px;font-weight: 600">
          合计：<span style="color: #ed3f14"> {{ addmoney }} </span> 元
        </div>
      </Col>
    </Row>
    <component :is="componentName"></component>
  </div>
</template>

<script>
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
      zrs: 0,
      zjes: 0,
      zsc: 0,
      addmoney: 0,
      pagerUrl: this.apis.lcjl.jlTj,
      choosedItem: null,
      componentName: '',
      JGList: [{val: '100', label: '所有考场'}],
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
          {title: '教练员', key: 'jlXm'},
          {title: '时长', key: 'sc', minWidth: 80, default: '0'},
          {title: '人数', key: 'xySl', minWidth: 80, default: '0'},
          {
            title: '应收', key: 'clBh', render: (h, p) => {
              return h('div', p.row.clBh + '元')
            }
          },
          {
            title: '实收', minWidth: 90, default: '0',
            render: (h, p) => {
              return h('div', p.row.zj + '元')
            }
          },

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
        lcLx: '',
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
      this.param.lcKm = this.lcKm
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
        window.open(this.apis.url + '/api/lcjl/jlExcel?token=' + token + '&userid=' + userid + '&' + p);
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
        this.addmoney = 0;
        this.zrs = 0;
        for (let r of list) {
          this.zjes += parseInt(r.clBh);
          this.zsc += r.sc;
          this.zrs += parseInt(r.xySl);
          r.sc = this.parseTime(r.sc)
          this.addmoney += r.zj;
        }
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
