<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <div style="display: flex;justify-content: space-between;align-items: center">
      <div style="margin-left: 60%">
        <Select v-if="JGList.length > 1" v-model="param.jgdmLike" @on-change="getData">
          <Option v-for="item in JGList" :value="item.val">{{ item.label }}</Option>
        </Select>
      </div>
      <Select v-model="param.lcKm" style="width: 100px;text-align: left; margin-right: 5px" clearable
              @on-change="getData" placeholder="科目">
        <Option v-for="item in kmList" :value="item.val"> {{ item.label }}</Option>
      </Select>
      <Select v-model="param.lcLx" style="width: 100px;text-align: left;margin-right: 5px" clearable
              @on-change="getData"
              placeholder="计费类型">
        <Option v-for="item in lcLxList" :value="item.val"> {{ item.label }}</Option>
      </Select>
      <DatePicker v-model="dateRange.kssj"
                  @on-change="() =>{param.kssjInRange = v.util.dateRangeChange(dateRange.kssj);v.getData()}" confirm
                  format="yyyy-MM-dd"
                  type="daterange" :placeholder="'请输入'" style="width: 200px"
                  split-panels></DatePicker>

      <Button type="primary" @click="getData" style="margin-left: 10px;">
        <Icon type="md-search"></Icon>
        <!--查询-->
      </Button>
      <Button type="primary" @click="exportExcel" style="margin-left: 10px;">
        <Icon type="ios-cloud-download"/>
      </Button>
    </div>
    <table-area :parent="v" :TabHeight="AF.getPageHeight()-240" :pager="false"></table-area>
    <Row>
      <Col span="8" align="right">
        <div style="font-size: 15px;font-weight: 600">
          时长合计：<span style="color: #ed3f14"> {{ zsc }} </span> 分钟
        </div>
      </Col>
      <Col span="8" align="right">
        <div style="font-size: 15px;font-weight: 600">
          应收合计：<span style="color: #ed3f14"> {{ zjes }} </span> 元
        </div>
      </Col>
      <Col span="8" align="right">
        <div style="font-size: 15px;font-weight: 600">
          合计：<span style="color: #ed3f14"> {{ addmoney }} </span> 元
        </div>
      </Col>
    </Row>
  </div>
</template>

<script>

import Cookies from "js-cookie";

export default {
  name: 'dhtj',
  data() {
    return {
      v: this,
      zsc: 0,
      zjes: 0,
      addmoney: 0,
      JGList: [{val: '100', label: '所有考场'}],
      lcLxList: [
        {val: '00', label: '计时'}, {val: '10', label: '按把'}, {val: '20', label: '培优'}, {val: '30', label: '开放'}
      ],
      kmList: [{val: '2', label: '科二'}, {val: '3', label: '科三'}],
      param: {
        jgdmLike: '',
        lcKm: '',
        lcLx: ''
      },
      props: {
        lcKm: {
          type: String,
          default: ''
        }
      },
      dateRange: {
        kssj: ''
      },
      tableColumns: [
        {
          type: 'index', minWidth: 60, align: 'center', title: '序号'
        },
        {
          title: '队号',
          key: 'jlJx',
          align: "center",
          sortable: true
        },
        {
          title: '时长',
          key: 'sc',
          align: 'center',
          render: (h, p) => {
            return h('div', p.row.sc + '分钟')
          }
        },
        {
          title: '应收',
          key: 'clBh',
          align: 'center'
        },
        {
          title: '实收',
          key: 'zj',
          align: 'center'
        }
      ],
      pageData: [],
      filterList: []
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
    this.getData();
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
    getData() {
      this.zjes = 0;
      this.zsc = 0;
      this.addmoney = 0;
      this.filterList = []
      this.$http.post('/api/lcjl/getDhTj', this.param).then(res => {
        if (res.result) {
          this.pageData = res.result
          for (let v of res.result) {
            this.zsc += v.sc
            this.addmoney += v.zj
            this.zjes += parseInt(v.clBh)
          }
        } else {
          this.pageData = []
        }
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
      window.open(this.apis.url + '/api/lcjl/exportDhTj?token=' + token + "&userid=" + userid + "&" + p);
    }
  }

}
</script>
