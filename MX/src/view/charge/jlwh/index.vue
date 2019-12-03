<template>
  <div class="boxbackborder box_col">
    <!--<pager-tit title="模训记录"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          教练维护
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
        <Input size="large" v-model="param.jlXm" clearable placeholder="请输入教练员姓名"
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
        <!--<table-area :parent="v" :TabHeight="AF.getPageHeight()-300"></table-area>-->
        <!--<component :is="componentName"></component>-->
    <Table :height="AF.getPageHeight()-240"
           size="small"
           @on-select="tabcheck"
           @on-select-cancel="tabcheck"
           @on-select-all="tabcheck"
           @on-select-all-cancel="tabcheck"
           :columns="tableColumns" :data="pageData"></Table>
    <!--      <table-area :parent="v"></table-area>-->
    <Row class="margin-top-10 pageSty">
      <div style="text-align: right;padding: 6px 0">
        <Page :total=param.total
              :current=param.pageNum
              :page-size=param.pageSize
              :page-size-opts=[8,10,15,20,30,40]
              show-total
              show-elevator
              show-sizer
              placement='top'
              @on-page-size-change='(n)=>{pageSizeChange(n)}'
              @on-change='(n)=>{pageChange(n)}'>
        </Page>
      </div>
    </Row>
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
        apiRoot: '/api/wxjl/pager',
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
          {title: '驾校', key: 'jlJx', searchKey: 'jlJxLike', minWidth: 90,
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
              var _self =  this.$options.parent.parent
              _self.param.lx=value[0]?value[0]:''
              _self.util.getPageData(_self)
            },
          },
          {title: '教练员', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 90},
          {
            title: '车号', key: 'clBh', searchKey: 'clBh', minWidth: 90, render: (h, p) => {
              return h('div', p.row.clBh)
            }
          },
          {title: '车型', key: 'jlCx', minWidth: 90, align: 'center',
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
              var _self =  this.$options.parent.parent
              if(value[0]==='0'){
                _self.param.jlCxIn='A,A1,A2,A3,B,B1,B2'
              }else if(value[0]==='1'){
                _self.param.jlCxIn='C,C1,C2'
              }
              else _self.param.jlCxIn=''
              _self.util.getPageData(_self)
            },
          },
          {
            title: '备注',
            minWidth: 250,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.bz)
            }
          }
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
          jssjIsNotNull: '1',
          total: 0,
          kssjInRange: '',
          zhLike: '',
          pageNum: 1,
          pageSize: 15
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
      this.getData()
      // this.util.initTable(this);
    },
    methods: {
      chpageNum() {
        this.param.pageNum = 1
      },
      getData(){
        this.$http.get('/api/lcwxjl/query', {params: {jlJx: val,notShowLoading: "true"}}).then((res) => {
          console.log(res);
          this.wxJL = res.result
        })
        // this.$http.get('/api/wxjl/pager',{param:this.param}).then((response) => {
        //     if (code == 200) {
        //
        //     } else {
        //       this.$Message.error(msg);
        //     }
        //   },
        //   (error) => {
        //     this.$Message.error('网络异常');
        //   }).then((next) => {
        // });
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
