<template>
  <div class="boxbackborder box_col" style="padding-top:16px">
    <div style="text-align: right;width: 100%">
      <DatePicker v-model="dateRange.kssj"
                  @on-change="kssjInRange = v.util.dateRangeChange(dateRange.kssj)" confirm format="yyyy-MM-dd"
                  type="daterange" split-panels :placeholder="'请输入'" style="width: 200px"></DatePicker>
      <Button type="primary" @click="getData" style="margin-left: 10px;">
        <Icon type="md-search"></Icon>
        <!--查询-->
      </Button>
      <!--<Button type="primary" @click="doPrint" style="margin-left: 10px;">打印</Button>-->
      <Button type="primary" @click="exportExcel" style="margin-left: 10px;">
        <Icon type="ios-cloud-download"/>
      </Button>
    </div>
    <div class="box_col_100" id="page1" style="width: 100%">
      <div style="text-align: center;position: relative">
        <span style="font-weight: 600;font-size: 20px;">
          收支统计
        </span>
        <span style="position: absolute;font-size: 15px;margin-top: 5px;right: 0">
          时间段：{{kssjInRange.split(',')[0].substring(0, 10)}}至{{kssjInRange.split(',')[1].substring(0, 10)}}
        </span>
      </div>
      <table border="1" cellpadding="0" cellspacing="0" style="width: 100%;text-align: center;">
        <thead>
        <tr>
          <td rowspan="2">日期</td>
          <td colspan="4">科二</td>
          <td colspan="4">科三</td>
          <td colspan="2">财务</td>
          <td rowspan="2">总计</td>
        </tr>
        <tr>
          <td>计时</td>
          <td>培优</td>
          <td>开放日</td>
          <td>小计</td>
          <td>计时</td>
          <td>培优</td>
          <td>按把</td>
          <td>小计</td>
          <td>充值卡</td>
          <td>返点</td>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item,index) in list" :key="index">
          <td v-for="(it,index) in item.split(',')">
            {{it}}
          </td>
        </tr>
        <tr>
          <td>合计</td>
          <td style="font-weight: 800">{{total[0] | GS }}</td>
          <td style="font-weight: 800">{{total[1] | GS }}</td>
          <td style="font-weight: 800">{{total[2] | GS }}</td>
          <td style="font-weight: 800">{{total[3] | GS }}</td>
          <td style="font-weight: 800">{{total[4] | GS }}</td>
          <td style="font-weight: 800">{{total[5] | GS }}</td>
          <td style="font-weight: 800">{{total[6] | GS }}</td>
          <td style="font-weight: 800">{{total[7] | GS }}</td>
          <td style="font-weight: 800">{{total[8] | GS }}</td>
          <td style="font-weight: 800">{{total[9] | GS }}</td>
          <td style="font-weight: 800">{{total[10]  | GS }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <component :is="componentName"></component>
  </div>
</template>

<script>

  import print from './print'
  import Cookies from 'js-cookie'
  import mixin from '@/mixins'

  export default {
    name: 'char',
    mixins:[mixin],
    components: {print},
    data() {
      return {
        v: this,
        pagerUrl: this.apis.lcjl.getAllLog,
        choosedItem: null,
        componentName: '',
        list: [],
        dateRange: {
          kssj: ''
        },
        kssjInRange: '',
        param: {
          start:'',
          end:''
        },
        total: [],
      }
    },
    created() {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      this.dateRange.kssj = [start, end]
      var d = start;
      var c = end;
      var datetimed = this.AF.trimDate(start) + ' ' + '00:00:00';
      var datetimec = this.AF.trimDate() + ' 23:59:59';
      this.kssjInRange = datetimed + ',' + datetimec

      // this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      // this.kssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      this.getData();
    },
    methods: {
      exportExcel() {
        let p = '';
        this.param.start = this.kssjInRange.split(',')[0].substring(0, 10);
        this.param.end = this.kssjInRange.split(',')[1].substring(0, 10);
        for (let k in this.param) {
          p += '&' + k + '=' + this.param[k];
        }
        p = p.substr(1);
        let accessToken = JSON.parse(Cookies.get('accessToken'));
        let token = accessToken.token;
        let userid = accessToken.userId;
        window.open(this.apis.url + '/api/lcjl/exportSec?token='+token+'&userid='+userid+'&' + p);
      },
      doPrint(how) {
        this.componentName = 'print';
      },
      getData() {
        this.list = [];
        this.param.start = this.kssjInRange.split(',')[0].substring(0, 10);
        this.param.end = this.kssjInRange.split(',')[1].substring(0, 10);
        this.$http.post('/api/lcjl/statisSec', this.param).then((res) => {
          if (res.code == 200) {
            if (res.result) {
              this.list = res.result;
              this.total = res.message.split(',')
            }
          } else {
            this.$Message.error(res.message);
          }
        })
      },
    }
  }
</script>

<style scoped>
  table td{
    padding: 5px 2px !important;
    font-size: 13px;
  }
</style>
