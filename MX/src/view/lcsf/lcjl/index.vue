<template>
  <div class="boxbackborder box_col">
    <!--<pager-tit title="模训记录"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          模训记录
        </div>
      </MenuItem>
    </Menu>
    <Row type="flex" justify="end" :gutter="8" style="margin:8px 0;">
      <!--        <Col span="6" style="padding: 10px 20px">-->
      <!--          <Button type="warning" @click="plzf">批量结算</Button>-->
      <!--        </Col>-->


      <!--<Col span="5" style="margin-right: -40px">-->
      <DatePicker v-model="dateRange.kssj"
                  style="margin-right: 5px"
                  @on-change="param.kssjInRange = v.util.dateRangeChange(dateRange.kssj)"
                  @on-open-change="pageSizeChange(param.pageSize)"
                  format="yyyy-MM-dd"
                  split-panels
                  type="daterange" :placeholder="'请输入时间'"></DatePicker>
      <!--</Col>-->
      <Col span="3">
        <Input size="large" v-model="param.jlJx" clearable placeholder="请输入驾校"
               @on-enter="getData"/>
      </Col>
      <Col span="3">
        <Input size="large" v-model="param.jlXm" clearable placeholder="请输入教练员姓名"
               @on-enter="getData"/>
      </Col>
      <Col span="3">
        <Input size="large" v-model="param.clBh" clearable placeholder="请输入车号"
               @on-enter="getData"/>
      </Col>
      <Col span="3">
        <Input size="large" v-model="param.zgXm" clearable placeholder="请输入安全员"
               @on-enter="getData"/>
      </Col>
      <Col span="1" align="center">
        <Button type="primary" @click="getData">
          <Icon type="md-search"></Icon>
          <!--查询-->
        </Button>
      </Col>
    </Row>
        <!--<search-bar :parent="v" :show-create-button="false" :showDownLoadButton="false"></search-bar>-->
        <table-area :parent="v" :TabHeight="AF.getPageHeight()-260"></table-area>
        <component :is="componentName"></component>
  </div>
</template>

<script>
  // import formData from './formModal.vue'

  export default {
    name: 'char',
    components: {},
    data() {
      return {
        v: this,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        componentName: '',
        searchBarButtons: [
          {title: '打印', click: 'print'}
        ],
        tableColumns: [
          {
            type: 'index2', align: 'center', minWidth: 80, title: '序号',
            render: (h, params) => {
              return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
            }
          },
          {
            title: '科目', key: 'lcKm', minWidth: 100,
            render: (h, p) => {
              switch (p.row.lcKm) {
                case '2':
                  return h('div', '科目二')
                  break;
                case '3':
                  return h('div', '科目三')
                  break;
              }
            },
            filters: [
              {
                label: '科目二',
                value: '2'
              },
              {
                label: '科目三',
                value: '3'
              },
            ],
            filterMultiple: false,
            filterMethod(value, row) {
              return row.lcKm === value;
            },
          },
          {title: '驾校', key: 'jlJx', searchKey: 'jlJxLike', minWidth: 90},
          {title: '教练员', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 90},
          {
            title: '车号', key: 'clBh', searchKey: 'clBh', minWidth: 90, render: (h, p) => {
              return h('div', p.row.clBh)
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
          {title: '车型', key: 'jlCx', minWidth: 60, align: 'center',},
          {
            title: '类型', minWidth: 90,
            render: (h, p) => {
              switch (p.row.lcLx) {
                case '00':
                  return h('div', '计时')
                  break;
                case '10':
                  return h('div', '按把')
                  break;
                case '20':
                  return h('div', '培优')
                  break;
                case '30':
                  return h('div', '开放日')
                  break;
              }
              return h('div', s)
            }
          },
          {title: '开始时间', key: 'kssj', minWidth: 140},
          {title: '结束时间', key: 'jssj', minWidth: 140},
          {title: '时长', key: 'sc', minWidth: 80, defaul: '0'},
          {
            title: '应收', minWidth: 90, defaul: '0', align: 'center',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元');
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
            title: '状态', minWidth: 120, render: (h, p) => {
              let s = '';
              if (!p.row.kssj || p.row.kssj === '') {
                s = '预约中'
              } else if ((p.row.kssj && p.row.kssj.length > 0) && (!p.row.jssj || p.row.jssj == '')) {
                s = '训练中'
              } else {
                s = '已结束'
              }
              return h('div', s);
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
          // {
          //   title: '返点状态', key: 'fdZt', minWidth: 110, render: (h, p) => {
          //     let s = this.dictUtil.getValByCode(this, 'ZDCLK1047', p.row.fdZt);
          //     return h('Tag', {
          //       props: {
          //         type: 'dot',
          //         color: p.row.fdZt == '10' ? 'success' : 'error'
          //       }
          //     }, s)
          //   }
          // },
          {title: '安全员', key: 'zgXm', searchKey: 'zgXmLike', minWidth: 100},
          // {title: '教练类型', key: 'jlLx', dict: 'jllx',minWidth:120},
          // {title:'操作',render:(h,p)=>{
          //     let buttons = [];
          //     buttons.push(this.util.buildeditButton(this,h,p));
          //     buttons.push(this.util.buildDeleteButton(this,h,p.row.yhid));
          //     return h('div',buttons);
          //   }
          //   },

        ],
        pageData: [],
        param: {
          orderBy: 'jssj desc',
          kssjIsNotNull: '1',
          total: 0,
          kssjInRange: '',
          zhLike: '',
          pageNum: 1,
          pageSize: 8
        },
        dateRange: {
          kssj: ''
        },
      }
    },
    created() {
      // this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      // this.param.kssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'

      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      this.dateRange.kssj = [start, end]
      var d = start;
      var c = end;
      var datetimed = this.AF.trimDate(start) + ' ' + '00:00:00';
      var datetimec = this.AF.trimDate() + ' 23:59:59';
      this.param.kssjInRange = datetimed + ',' + datetimec
      this.util.initTable(this);
    },
    methods: {
      chpageNum() {
        this.param.pageNum = 1
      },
      getData(){
        this.util.getPageData(this)
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
        for (let r of list) {
          r.sc = this.parseTime(r.sc)
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)
        }
      },
      print() {
        console.log('print');
      }
    }
  }
</script>
