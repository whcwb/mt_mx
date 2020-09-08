<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <!--<pager-tit title="驾校统计"></pager-tit>-->
    <div style="text-align: right;width: 100%">
      <Row>
        <Col span="17">
          <div style="min-height: 1px"></div>
        </Col>
        <Col span="2" style="text-align: left">
          <Select v-if="JGList.length > 1" v-model="param.jgdmLike" @on-change="v.util.getPageData(v)">
            <Option v-for="item in JGList" :value="item.val">{{item.label}}</Option>
          </Select>
          <div v-else style="min-height: 1px"></div>
        </Col>
        <Col span="5">
          <DatePicker v-model="dateRange['tjsj']"
                      @on-ok="v.util.getPageData(v)"
                      @on-change="param['tjsj'] = v.util.dateRangeChange(dateRange['tjsj'])" confirm format="yyyy-MM-dd"
                      type="daterange" :placeholder="'请输入'" style="width: 200px"></DatePicker>
          <Button type="primary" @click="v.util.getPageData(v)" style="margin-left: 10px;">
            <Icon type="md-search"></Icon>
            <!--查询-->
          </Button>
          <Button type="primary" @click="exportExcel" style="margin-left: 10px;">
            <Icon type="ios-cloud-download"/>
          </Button>
        </Col>
      </Row>
    </div>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="24" align="right">
        <div style="font-size: 15px;font-weight: 600">
          合计：<span style="color: #ed3f14"> {{addmoney}} </span> 元
        </div>
      </Col>
    </Row>
    <component :is="componentName"></component>
  </div>
</template>

<script>
import print from './print'
import Cookies from 'js-cookie'

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
        addmoney: 0,
        pagerUrl: this.apis.lcjl.jxtj,
        choosedItem: null,
        componentName: '',
        JGList: [{val: '100', label: '所有考场'}],
        searchBarButtons: [
          {title: '打印', click: 'print'}
        ],
        dateRange: {
          tjsj: ''
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
          {title: '时长', key: 'lcSc', minWidth: 80, defaul: '0'},
          {title: '人数', key: 'xySl', minWidth: 80, defaul: '0'},
          {
            title: '实收', minWidth: 90, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元')
            },
          },
        ],
        pageData: [],
        pager: false,
        specialPageSize: 9999,
        param: {
          total: 0,
          zhLike: '',
          pageNum: 1,
          lcKm: '',
          pageSize: 8,
          zfzt: '10',
          jgdmLike: '100'
        },
      }
    },
    created() {
      if(Cookies.get("daterange")!=undefined&&Cookies.get("daterange")!=''){
        this.dateRange.tjsj = Cookies.get("daterange").split(',')
        this.param.tjsj = Cookies.get("daterange")
      }else {
        this.dateRange.tjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
        this.param.tjsj = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
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
        window.open(this.apis.url + '/api/lcjl/jxtjExcel?token=' + token + "&userid=" + userid + "&" + p);
      },
      afterPager(list) {
        this.addmoney = 0
        for (let r of list) {
          // r.lcSc = this.parseTime(r.lcSc)
          this.addmoney += parseInt(r.lcFy);
        }
      },
      getTime(s) {
        s = parseInt(s);
        let h = parseInt(s / 60);
        let m = s % 60;
        let r = '';
        if (h > 0) r += h + '小时'
        return r + m + '分钟'
      }
    }
  }
</script>
