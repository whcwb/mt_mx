<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <!--<pager-tit title="驾校统计"></pager-tit>-->
    <div style="text-align: right;width: 100%">
      <DatePicker v-model="dateRange['tjsj']"
                  @on-ok="v.util.getPageData(v)"
                  @on-change="param['tjsj'] = v.util.dateRangeChange(dateRange['tjsj'])" confirm format="yyyy-MM-dd"
                  type="daterange" :placeholder="'请输入'" style="width: 200px"></DatePicker>
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
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="24" align="right">
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
      searchBarButtons: [
        {title: '打印', click: 'print'}
      ],
      dateRange: {
        tjsj: ''
      },
      tableColumns: [
        {title: '序号', type: 'index',fixed: 'left',minWidth:80},
        {
          title: '驾校', key: 'jx',fixed: 'left',minWidth:100,
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
          title: '科二',
          align: 'center',
          children: [
            {
              title: '计时',
              key:'k2js',
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key:'k2Yfd',
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key:'k2Dfd',
              align: 'center',minWidth:80
            },
            {
              title: '培优',
              key: "k2py",
              align: 'center',minWidth:80
            },
            {
              title: "已返",
              key: "k2pyYfd",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k2pyDfd",
              align: 'center',minWidth:80
            },
            {
              title: '开放日',
              key: "k2Kf",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k2KfYfd",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k2KfDfd",
              align: 'center',minWidth:80
            },
            {
              title: '小计2',
              key: "k2Xj",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k2YfdXj",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k2DfdXj",
              align: 'center',minWidth:80
            }
          ]
        },
        {
          title: '科三',
          align: 'center',
          children: [
            {
              title: '计时',
              key: "k3js",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k3Yfd",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k3Dfd",
              align: 'center',minWidth:80
            },
            {
              title: '培优',
              key: "k3py",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k3pyYfd",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k3pyDfd",
              align: 'center',minWidth:80
            },
            {
              title: '按把',
              key: "k3Ab",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k3AbYfd",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k3AbDfd",
              align: 'center',minWidth:80
            },
            {
              title: '小计3',
              key: "k3Xj",
              align: 'center',minWidth:80
            },
            {
              title: '已返',
              key: "k3YfdXj",
              align: 'center',minWidth:80
            },
            {
              title: '待返',
              key: "k3DfdXj",
              align: 'center',minWidth:80
            }
          ]
        },
        {
          title: '总计',
          key: "zj",
          fixed: 'right',
          align: 'center',minWidth:80
        },
        {
          title: '已返',
          fixed: 'right',
          key: "yfdZj",
          align: 'center',minWidth:80
        },
        {
          title: '待返',
          fixed: 'right',
          key: "dfdZj",
          align: 'center',minWidth:80
        }
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
        zfzt: '10'
      },
    }
  },
  created() {
    if (Cookies.get("daterange") != undefined && Cookies.get("daterange") != '') {
      this.dateRange.tjsj = Cookies.get("daterange").split(',')
      this.param.tjsj = Cookies.get("daterange")
    } else {
      this.dateRange.tjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.tjsj = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
    }

    this.param.lcKm = this.lcKm
    this.util.initTable(this);
  },
  methods: {
    // parseTime(s) {
    //   s = parseInt(s);
    //   let h = parseInt(s / 60);
    //   let m = s % 60;
    //   let r = '';
    //   if (h != 0) r += h + '小时'
    //   return r + m + '分钟'
    // },
    exportExcel() {
      let p = '';
      for (let k in this.param) {
        p += '&' + k + '=' + this.param[k];
      }
      p = p.substr(1);
      let accessToken = JSON.parse(Cookies.get('accessToken'));
      let token = accessToken.token;
      let userid = accessToken.userId;
      window.open(this.apis.url + '/api/lcjl/downloadNewJxtj?token=' +token + "&userid=" + userid+"&"+ p);
    },
    afterPager(list) {
      this.addmoney = 0
      for (let r of list) {
        this.addmoney += parseInt(r.zj);
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
<style scoped>
.ivu-table th.test-name {
  background: #f40;
}
</style>
