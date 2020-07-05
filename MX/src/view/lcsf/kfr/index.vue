<template>
  <div class="box_col">
    <!--<pager-tit title="开放日训练" style="float: left"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          开放日训练
        </div>
      </MenuItem>
    </Menu>
    <Row style="" type="flex" align="bottom">
      <Col span="24">
        <Row type="flex" justify="end" :gutter="8">
          <DatePicker v-model="dateRange.cjsj"
                      @on-change="param.cjsjInRange = v.util.dateRangeChange(dateRange.cjsj)"
                      @on-ok="v.util.getPageData(v)"
                      confirm
                      format="yyyy-MM-dd"
                      style="margin-right: 5px"
                      split-panels
                      type="daterange" :placeholder="'请输入时间'"></DatePicker>
          <Col span="3">
            <Input size="large" v-model="param.jlXmLike" clearable placeholder="请输入教练姓名"
                   @on-enter="v.util.getPageData(v)"/>
          </Col>
          <Col span="1" align="center" style="margin-right: 5px">
            <Button type="primary" @click="v.util.getPageData(v)">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </Col>
          <Col span="1" align="center">
            <Button type="primary" @click="ifFinish=true,formData.zddm='K2KF';faCar('kf')">
              <Icon type="md-add"></Icon>
              <!--查询-->
            </Button>
          </Col>
        </Row>
      </Col>
    </Row>
    <div>
      <Row>
        <table-area :pager="false" :TabHeight="AF.getPageHeight()-230" :parent="v"></table-area>
      </Row>

      <Row style="display: flex;align-items: center;height: 36px">
        <Col span="3" align="left">
          <i-switch v-model="switch1"></i-switch>
        </Col>
        <Col span="21" align="right" v-if="switch1">
          <span>
            <span style="font-size: 15px;font-weight: 600">
            人数：<span style="color: #ed3f14"> {{rs}} </span> 人
            </span>
          &nbsp&nbsp&nbsp
            <span style="font-size: 15px;font-weight: 600">
            合计：<span style="color: #ed3f14"> {{hj | GS}} </span> 元
            </span>
            </span>
        </Col>
      </Row>

    </div>
    <Modal
      title="分配车辆"
      v-model="DrawerVal"
      :closable="false"
      width="800"
      :mask-closable="false">
      <div slot="header">
        <div class="box_row">
          <div v-if="carMess">
            <Tag type="border" style="font-size: 24px;font-weight:bold;padding: 5px;height: 36px;" color="error">
              {{carMess.clCx}}
            </Tag>
          </div>
          <div style="font-size: 16px;margin-right: 28px;margin-top: 7px">
            <h2 v-if="mxlx=='kf'">开放训练</h2>
          </div>
        </div>
      </div>
      <Form :model="formData" label-position="top">
        <Row>
          <Col span="24"></Col>
        </Row>
        <Row :gutter="24">
          <Col span="17">
            <div style="float: left">
              <FormItem label="教练员" style="width: 250px">
                <Select v-model="formData.jlId"
                        filterable
                        clearable
                        remote
                        @on-query-change="searchJly"
                        @on-change="changeTest"
                        ref="jlySelect"
                >
                  <Option v-for="(it,index) in searchCoachList" :value="it.value" :key="index">{{it.label}}</Option>
                </Select>
              </FormItem>
            </div>
            <div style="padding-top: 22px;">
              <Button type="primary" @click="compName='addjl'">
                <Icon type="md-add"/>
              </Button>
            </div>
          </Col>
        </Row>
        <Row :gutter="32">
          <Col span="12">
            <div style="float: left">
              <FormItem label="计费套餐" label-position="top">
                <Select v-model="formData.zddm" @on-change="lcFyChange" style="width:250px">
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="!it.zddm.includes('K2JS')">
                    {{it.by9}}-{{it.zdmc}}元{{it.by10=='0'?'':('-'+it.by10+'分钟')}}
                  </Option>
                </Select>
              </FormItem>
            </div>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm!=undefined&&formData.zddm.includes('K2PY')">
          <Card>
            <p slot="title">学员信息</p>
            <p>
              <Row v-for="(item,index) in AMess" :key="index">
                <Col span="3">
                  <RadioGroup v-model="item.cartype">
                    <Radio label="C1"></Radio>
                    <Radio label="C2"></Radio>
                  </RadioGroup>
                </Col>
                <Col span="4" :class-name="'colsty'">
                  <Input type="text" size="default" v-model="item.xyXm" placeholder="学员姓名"/>
                </Col>
                <Col span="4" :class-name="'colsty'">
                  <Input type="textarea" :autosize="{minRows: 1,maxRows: 1}"
                         size="default" v-model="item.xyDh" placeholder="学员联系电话"/>
                </Col>
                <Col span="8" :class-name="'colsty'">
                  <Input type="textarea" :autosize="{minRows: 1,maxRows: 1}"
                         size="default" v-model="item.bz" placeholder="身份证号码"/>
                </Col>
                <Col span="2" v-if="AMess.length>1">
                  <Button size="default" type="warning" @click="remove(index)">删除</Button>
                </Col>
                <Col span="2" align="center">

                  <Button type="info" icon="md-add"
                          @click="pushmess"
                  >
                  </Button>
                </Col>
              </Row>
            </p>
          </Card>
        </Row>
        <component :is="compName" :jxmc="jlJx"
                   @SaveOk="addjlSaveOk"
                   @colse="clearYY"
                   @remove="getCoachList('',true)"
                   @JLRowClick="JLRowClick"
                   @jxSeljxSel="(val)=>{getCoachList('',true)}"></component>

        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm && formData.zddm.startsWith('K2KF')">
          <Col span="11">
            <FormItem label="学员人数" label-position="top">
              <InputNumber :min="1" v-model="formData.xySl" @keyup.enter.native="save"
                           style="width: 250px"></InputNumber>
            </FormItem>
          </Col>
        </Row>


        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='py'">
          <Col span="12">
            <FormItem label="金额" label-position="top">
              <CheckboxGroup v-model="formData.lcFy">
                <Checkbox label="900"></Checkbox>
              </CheckboxGroup>
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='py'">
          <Col span="12">
            <FormItem label="安全员姓名" label-position="top">
              <Input v-model="formData.zgXm"/>
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">取消</Button>
        <Button type="primary" @click="save">确定</Button>
        <Button type="primary" @click="yy" v-if="mxlx==='kk'">预约</Button>

      </div>
    </Modal>
    <component :is="componentName" :printClose="printClose" :hisPrintMess="hisPrintMess"
               @SaveOk="addjlSaveOk"
               @colse="clearYY"
               @remove="getCoachList('',true)"
               @JLRowClick="JLRowClick"
               @jxSeljxSel="(val)=>{getCoachList('',true)}"></component>


    <Modal
      v-model="updateAQY"
      :closable="false"
      width="500"
      :mask-closable="false">
      <div slot="header">
        <div class="box_row">
          <div style="font-size: 16px;margin-right: 28px;margin-top: 7px">
            <h2>{{updateAQYtitle}}安全员</h2>
          </div>
        </div>
      </div>
      <Row :gutter="32" style="padding-top: 5px">
        <Col span="12">
          安全员
          <Input v-model="aqyItem.zgXm"/>
        </Col>
      </Row>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="updateAQY=false,aqyItem.zgXm=aqyItem.id=''">取消</Button>
        <Button type="primary" @click="update">确定</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
  import carCard from '../comp/carCard'
  import jlwh from '../comp/jlWh'
  import addjl from '../comp/addJL'
  import carStatistics from '../statistics/carStatistics'
  import keyypd from '../comp/keyypd'
  import print from './comp/print'
  import radioCar from '../comp/RadioCar'
  //还车
  import giveCar from '../comp/readCard'
  import {mapMutations} from 'vuex'
  import moment from 'moment'
  import Cookies from 'js-cookie'
  import printNew from '../../../components/printNew'
  import mixin from '@/mixins'

  export default {
    name: "index",
    mixins: [mixin],
    components: {
      carCard, jlwh, addjl,
      print, radioCar, carStatistics, printNew,
      keyypd,
    },
    data() {
      return {
        Pmess: {},
        AMess: [
          {cartype: 'C1'}
        ],
        hj: 0,
        rs: 0,
        mxlx: '',
        switch1: true,
        total: 0,
        ifFinish: false,
        giveCar: giveCar,
        v: this,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        tableColumns: [
          {
            title: '序号', align: 'center', minWidth: 50,
            render: (h, p) => {
              return h('div', p.index + 1)
            },
          },
          {title: '驾校', key: 'jlJx', align: 'center', minWidth: 90},
          {title: '教练员', key: 'jlXm', align: 'center', searchKey: 'jlXmLike', minWidth: 90},
          {title: '教练员电话', align: 'center', key: 'jlDh', minWidth: 80},
          {
            title: '类型', minWidth: 90, align: 'center',
            render: (h, p) => {
              if (p.row.lcLx == '30') {
                return h('div', p.row.zdxm.by9 + "-" + p.row.zdxm.by10 + "分钟")
              }
              return h('div', '培优-' + p.row.zdxm.zdmc + '元')
            },
            filters: [
              {
                label: '培优',
                value: '20'
              },
              {
                label: '开放日',
                value: '30',
              }
            ],
            filterMultiple: false,
            filterMethod(value, row) {
              return row.lcLx == value;
            }
          },
          {
            title: '学员数量', align: 'center', key: 'xySl', minWidth: 60, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.xySl + '人')
            },
          },
          {
            title: '练车费用', align: 'center', minWidth: 90, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元')
            },
          },
          {title: '创建时间', align: 'center', key: 'kssj', searchType: 'daterange', minWidth: 140},
          {
            title: '学员信息', align: 'center', minWidth: 250,
            className: 'no_padding',
            render: (h, params) => {
              let x = ''
              if (params.row.zddm.indexOf('PY') >= 0) {
                let a = params.row.xyDh.split(',')
                let b = params.row.xyZjhm.split(',')
                let d = params.row.xyXm.split(',')
                for (let i = 0; i < a.length; i++) {
                  let sfz = b[i]
                  let dh = a[i]
                  let cx = d[i].split('-')[1]
                  let xm = d[i].split('-')[0]

                  if (i == a.length - 1) {
                    x = "<label>" + x + xm + ',' + sfz + ',' + dh + ',' + cx + "</label>"
                  } else {
                    x = "<label>" + x + xm + ',' + sfz + ',' + dh + ',' + cx + "</label>" + "<br></br>";
                  }
                }
              }
              return h('div', {
                domProps: {
                  innerHTML: x
                },
              }, x);
            }
          },
          {
            title: '操作', fixed: 'right', width: 80, render: (h, p) => {
              let buttons = [];
              var v = this
              if (!p.row.kssj || p.row.kssj === '') {       //预约
                buttons.push(this.util.buildButton(this, h, 'warning', 'md-card', '制卡', () => {
                  this.faCar('kk')
                }));

                buttons.push(this.util.buildButton(this, h, 'error', 'md-close', '删除', () => {

                  this.swal({
                    title: '确定取消该学员的预约？',
                    type: 'warning',
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    showCancelButton: true
                  }).then(res => {
                    if (res.value) {
                      this.removeYY(p.row.id)
                    } else {
                    }
                  })
                }));
              } else {
                buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '打印票据', () => {
                  this.hisPrintMess = p.row
                  this.printClose = false
                  this.componentName = 'printNew'
                }));
              }
              if (p.row.lcLx == '20') {
                buttons.push(this.util.buildButton(this, h, 'info', 'ios-construct', '更改安全员', () => {
                  if (p.row.zgXm == '') {
                    this.aqyItem.zgXm = ''
                    this.updateAQYtitle = '添加'
                  } else {
                    this.updateAQYtitle = '更改'
                    this.aqyItem.zgXm = p.row.zgXm
                  }
                  this.aqyItem.id = p.row.id
                  this.updateAQY = true

                }, p.row.lcLx == '20' ? false : true));
              }

              return h('div', buttons);
            }
          }
        ],
        updateAQY: false,
        updateAQYtitle: '更改',
        aqyItem: {
          zgXm: '',
          id: ''
        },
        DrawerVal: false,
        compName: '',
        componentName: '',
        printClose: false,
        hisPrintMess: '',
        clId: '',
        showFQfzkp: false,
        formData: {
          zddm: 'K2KF',
          zgXm: '',
          lcKm: 2,
          lcLx: '30',
          cardNo: '',
          clBh: '',
          lcClId: '',
          jlJx: '',
          jlId: "",
          jlCx: 'C1',
          xyZjhm: '',
          xyXm: '',
          xyDh: '',
          xySl: null,
          // yhsc:'5',
          id: '',
          jlXm: '',
          jlDh: '',
          sc: ''
        },
        searchCoachList: [],
        loadingJly: false,
        yyrs: '0',
        bxJL: [],//本校
        wxJL: [],//外校
        jlJx: '',
        zxNum: 0,
        xxNum: 0,
        carList: [],
        coachList: [],
        param: {
          notShowLoading: 'true',
          orderBy: 'kssj desc',
          total: 0,
          lcKm: 2,
          lcLxIn: '20,30',
          cjsjInRange: '',
          zhLike: ''
        },
        pageData: [],
        specialPageSize: 99999999,
        dateRange: {
          cjsj: ''
        },
        showCAR: false,
        carMess: null,
        fylist: []
      }
    },
    watch: {
      DrawerVal: function (n, o) {
        var v = this
        if (n == false) {
          this.compName = ''
          this.formData = {}
          this.formData.xySl = ''
          this.formData.jlCx = 'C1'
          this.jlJx = ''
        } else {
        }
      },
      switch1: function (val) {
        Cookies.set('showMess', val)
      }
    },
    mounted() {
      this.switch1 = Cookies.get('showMess') === 'true' ? true : false
    },
    created() {
      this.dateRange.cjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      this.util.initTable(this);
      this.getCoachList()
      this.getCarList();
      this.getzdlist()
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
      remove(i) {
        this.AMess.splice(i, 1)
      },
      pushmess() {
        let a = JSON.parse(JSON.stringify(this.Pmess));
        this.AMess.push(a);
      },
      getWXXY(AMess) {
        if (this.formData.zddm.indexOf('K2PY') == -1) {
          return true
        }
        AMess = this.AMess
        let arrAMess = AMess.length - 1;
        let messarr = [];
        let dxarr = [];
        let sfzarr = [];
        let a = true
        for (let i = 0; i < AMess.length; i++) {
          if (AMess[i].xyXm == undefined || AMess[i].xyXm == '' || AMess[i].xyXm == null) {
            this.swal({
              title: '请填写学员姓名',
              type: 'error'
            })
            a = false
            break
          } else {
            messarr.push(AMess[i].xyXm + '-' + AMess[i].cartype)
            dxarr.push(AMess[i].xyDh)
            sfzarr.push(AMess[i].bz)
            if (i == arrAMess) {
              this.formData.xyXm = messarr.join(',');
              this.formData.xyDh = dxarr.join(',');
              this.formData.xyZjhm = sfzarr.join(',');
            }
          }

        }
        return a
      },
      scXY(e) {
        this.AMess = [{}];
        e = parseInt(e);
        for (let i = 1; i < e; i++) {
          this.AMess.push({})
        }
      },
      pr() {
        var api = 'https://www.baidu.com/s?wd=%E7%99%BE%E5%BA%A6%E7%9A%84%E7%BD%91%E5%9D%80ip&rsv_spt=1&rsv_iqid=0xd41114de00062d0d&issp=1&f=3&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=0&rsv_dl=ts_0&oq=vue%2520%25E6%258C%2587%25E5%25AE%259Aip%25E5%258F%2591%25E9%2580%2581axios&rsv_t=da21ZuhZm7lWkwXiSRKJoP0FazdoiDT9YLFiJfz636%2BAOuzA4nKH%2FNV87xMowp35sUca&inputT=4160&rsv_pq=9fc868c100415bd8&rsv_sug3=125&rsv_sug1=109&rsv_sug7=100&rsv_sug2=0&prefixsug=%25E7%2599%25BE%25E5%25BA%25A6%25E7%259A%2584&rsp=0&rsv_sug4=6824'
        this.$http.get(api).then((response) => {
        }).catch(function (error) {
        })
      },
      getzdlist() {
        this.$http.post('/api/lcjl/Tc', {km: '2'}).then((res) => {
          if (res.code == 200) {
            this.fylist = res.result
            for (let r of this.fylist) {
              r.editMode = false
              r.zdmc = parseInt(r.zdmc)
              r.by3 = parseFloat(r.by3)
              r.by4 = parseFloat(r.by4)
              if (r.zddm == 'k2JS') {
                r.by9 = '计时' + r.zdmc + '元/小时'
              }
              if (r.zddm == 'K2KF1') {
                r.by9 = '开放日1套餐' + r.zdmc + '元'
              }
              if (r.zddm == 'K2KF2') {
                r.by9 = '开放日2套餐' + r.zdmc + '元'
              }
              if (r.zddm == 'K2KF3') {
                r.by9 = '开放日3套餐' + r.zdmc + '元'
              }
            }

          }
        })
      },
      jump() {
        this.total = 0;
        for (let r of this.pageData) {
          let startTime = r.kssj;
          if (r.kssj.length < 19) {
            startTime += ":00";
          }
          let now = new Date();
          let duration = moment(moment(now) - moment(startTime));
          if ((r.kssj && r.kssj.length > 0) && (!r.jssj || r.jssj == '') && (r.lcLx != '20' || r.lcLx != '30')) {
            let min = parseInt(duration / 60000);
            r.sc = duration.subtract(8, 'hour').format("HH时mm分钟");//this.parseTime(min);
            r.lcFy = Math.round(min * 500 / 60);
            r.showlcFy = r.lcFy + '元';
          }
          this.total += parseInt(r.lcFy);
          if (!this.total) {
            this.total = 0;
          }
        }
      },
      parseTime(s) {
        s = parseInt(s);
        let h = parseInt(s / 60);
        let m = s % 60;
        let r = '';
        if (h != 0) r += h + '小时';
        return r + m + '分钟'
      },
      afterPager(list) {
        this.hj = 0
        this.rs = 0
        for (let r of list) {
          r.sc = this.parseTime(r.sc)
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)

          this.hj = this.hj + r.lcFy;
          this.rs = this.rs + r.xySl
        }
      },
      ...mapMutations([
        'set_LcTime',
        'Ch_LcTime'
      ]),
      getCarItemMess(it, index) {
        this.formData.lcClId = it.id
      },
      changeTest(val) {
      },
      searchJly(query) {
        if (query !== '') {
          this.loadingJly = true;
          setTimeout(() => {
            this.loadingJly = false;
            this.searchCoachList = this.coachList.filter(item => {
              return item.label.indexOf(query.toUpperCase()) != -1
            });
          }, 200);
        } else {
          // this.searchCoachList = [];
        }
      },
      clzt(zt) {

      },
      clearYY() {
        this.compName = '';
        this.componentName = '';
        this.getYYdj();
        this.getCarList();
        this.searchCoachList = [];
      },
      JLRowClick(row) {
        this.formData.jlId = row.id
      },
      close() {
        // this.showCAR = false;
        this.AMess = [{cartype: 'C1'}];
        this.carMess = null;
        this.formData = {};
        this.formData.jlCx = 'C1'
        this.XY = [];
        this.compName = '';
        this.DrawerVal = false;
        this.sfaemanlist = [];
        this.formData.lcLx = '00';
        this.searchCoachList = [];
        //清空下拉框内容
        this.$refs.jlySelect.clearSingleSelect();
      },
      yyClick(val, cx) {
        this.$refs.yyModel.show();
        this.formData.lcClId = val;
      },
      getYYdj() {
        this.$http.get('/api/lcjl/query', {
          params: {
            kssjIsNull: '1',
            orderBy: 'cjsj asc',
            lcKm: '2',
            notShowLoading: 'true'
          }
        }).then((res) => {
          if (res.code == 200) {
            if (res.result) {
              this.yyrs = res.result.length
            } else {
              this.yyrs = 0
            }
          }
        })
      },
      lcFyChange(v) {
        this.formData.zddm = v
      },
      faCar(name) {
        if (name === 'kk') {
          var v = this

          this.giveCar.readCard((key, mess) => {
            this.mxlx = name
            if (!key) {
              if (this.DrawerVal) {
                let v = this
                setTimeout(() => {
                  if (v.DrawerVal) {
                    this.faCar()
                  }
                }, 200)
              }
              if (v.showFQfzkp) {
                return;
              }
              v.showFQfzkp = true;
              v.swal({
                title: mess,
                type: 'error',
                confirmButtonText: '发车',
                cancelButtonText: '取消',
                showCancelButton: true
              }).then((res) => {
                if (res.value) {
                  v.showFQfzkp = false;
                  v.faCar()
                } else {
                  v.showFQfzkp = false;
                  v.showQfshowFQfzkpzkp = false;
                  v.DrawerVal = false
                  v.mxlx = ''
                }
              })
            } else {
              this.AF.carCard('2', mess, (type, res) => {
                if (type) {
                  if (res.result) {
                    //如果车辆已经绑卡   返回车辆信息
                    v.carMess = res.result
                    this.formData.lcClId = v.carMess.id
                  }
                  this.DrawerVal = true;
                  v.showFQfzkp = false;
                  this.formData.cardNo = mess;
                } else {
                  this.DrawerVal = false;
                  this.mxlx = ''
                  return
                }
              })
            }
          })
        } else {
          var v = this
          this.mxlx = name
          this.DrawerVal = true;
        }
      },
      readkar(callback) {
        if (!!window.ActiveXObject || "ActiveXObject" in window) {
        } else {
          this.swal({
            title: '请使用IE10以上的浏览器',
            type: 'warning',
            confirmButtonText: '关闭'
          })
          return
        }
        var v = this
        this.giveCar.readCard((key, mess) => {
          if (!key) {
            if (this.DrawerVal) {
              let v = this
              setTimeout(() => {
                if (v.DrawerVal) {
                  this.readkar()
                }
              }, 200)
            }
            if (v.showFQfzkp) {
              return;
            }
            v.showFQfzkp = true;
            v.swal({
              title: mess,
              type: 'error',
              confirmButtonText: '发车',
              cancelButtonText: '取消',
              showCancelButton: true
            }).then((res) => {
              if (res.value) {
                v.showFQfzkp = false;
                v.readkar()
              } else {
                v.showQfshowFQfzkpzkp = false;
                v.DrawerVal = false
              }
            })
          } else {
            v.showFQfzkp = false;
            this.formData.cardNo = mess
            callback && callback(true)
            this.save()
          }
        })
      },
      update() {
        if (this.aqyItem.zgXm == '') {
          this.$Message.info('请输入安全员姓名');
          return
        }
        this.$http.post('/api/lcjl/update', this.aqyItem).then(res => {
          if (res.code == 200) {
            this.updateAQY = false
            this.aqyItem = {}
            this.util.initTable(this);
            this.swal({
              title: '操作成功',
              type: 'success',
              confirmButtonText: '确定',
            })
          } else {
            this.$Message.info(res.message);
          }
        })
      },
      his(item) {//历史练车记录
        this.clId = item.id;
        this.componentName = 'carStatistics'
      },
      carClick(val) {
        this.getCoachList();
        this.formData.lcClId = val
        this.DrawerVal = true
      },
      addjlSaveOk(id) {
        this.getCoachList(id)
      },
      removeYY(id) {
        this.$http.post('/api/lcjl/remove/' + id).then((res) => {
          if (res.code == 200) {
            this.util.initTable(this);
            this.swal({
              title: '取消成功',
              type: 'success',
              confirmButtonText: '确定',
            })
          } else {
            this.swal({
              title: res.message,
              type: 'warning'
            })
          }
        })
      },
      getCarList() {//获取车辆
        this.param.clBh = this.formData.clBh
        this.zxNum = 0;
        this.xxNum = 0;
        this.$http.post('/api/lccl/getCar', {
          notShowLoading: 'true',
          pagerNum: 1,
          pageSize: 99999,
          clKm: "2",
          clBh: this.formData.clBh,
          orderBy: 'clZt asc,clBh asc,clCx asc',
          clZt: this.formData.clZt,
          clCx: this.formData.clCx
        }).then((res) => {
          if (res.code == 200) {
            this.carList = res.page.list
            for (let r of this.carList) {
              if (r.clZt === '01') {
                this.zxNum++;
              } else if (r.clZt === '00') {
                this.xxNum++;
              }
            }

            this.AF.Get_SERVER_Time((res) => {
              this.set_LcTime(res)
              this.IntervalKS
            })

          } else {
            this.$Message.info(res.message);
          }
        })
      },
      getCoachList(id, clear) {
        if (clear) {
          this.formData.jlId = '';
        }
        this.coachList = [];
        this.$http.get('/api/lcwxjl/query', {params: {notShowLoading: 'true'}}).then((res) => {
          this.wxJL = res.result
          if (res.code == 200 && res.result) {
            for (let r of res.result) {
              let py = this.util.parsePY(r.jlXm)
              this.coachList.push({label: r.jlJx + '_' + r.jlXm + '[' + py + ']' + '_' + r.jlLxdh, value: r.id});
            }
          }
          if (res.code == 200 && res.result && id) {
            this.formData.jlId = id
          }
        })
      },
      save() {//发车
        if (this.mxlx == 'kk') {
          this.formData.lcLx = ''
          this.formData.lcKm = 2
        }
        if (this.mxlx == 'py') {
          this.formData.lcLx = '20'

          this.formData.lcKm = 2
        }
        if (this.mxlx == 'kf') {
          this.formData.lcLx = '30'

          this.formData.lcKm = 2
        }
        delete this.formData.lcFy
        if (this.getWXXY()) {
          this.$http.post('/api/lcjl/save', this.formData).then(res => {
            if (res.code == 200) {
              this.DrawerVal = false;
              this.util.initTable(this);
              this.carMess = null
              this.AMess = [{cartype: 'C1'}];
              if (this.mxlx == 'py' || this.mxlx == 'kf') {
                //打印票据
                this.formData = JSON.parse(res.message)
                this.formData.sc = ''
                this.formData.yhsc = '5分钟'
                this.formData.kc = '科目二'
                this.formData.clBh = ''
                this.formData.lcKm = '2'

                this.hisPrintMess = this.formData
                this.printClose = this.ifFinish
                this.componentName = 'printNew'
                this.ifFinish = false

              }
            } else {
              this.formData.cardNo = null;
              this.swal({
                title: res.message,
                type: 'warning'
              })
            }

          }).catch(err => {
            this.formData = {
              zgXm: '',
              lcKm: 2,
              lcLx: '',
              lcFy: '900',
              cardNo: '',
              clBh: '',
              lcClId: '',
              jlJx: '',
              jlId: "",
              jlCx: 'C1',
              xyZjhm: '',
              xyXm: '',
              xyDh: '',
              // yhsc:'5',
              id: '',
              jlXm: '',
              jlDh: '',
              sc: ''
            }
          })
        }


        // }
      },
      yy() {
        //预约练车
        let appoint = this.formData.jlCx + ',' + '1' + ',' + ''
        this.$http.post('/api/lcjl/saveJl', {jlId: this.formData.jlId, appoint: appoint, lcKm: 2}).then((res) => {
          if (res.code == 200) {
            this.DrawerVal = false;
            this.AMess = [{}];
            this.formData = {};
            this.formData.lcLx = '00';
            this.swal({
              title: '已成功预约',
              type: 'success',
              showCancelButton: false,
              confirmButtonText: '确定',
            })
            this.util.initTable(this);
          } else {
            this.swal({
              title: res.message,
              type: 'warning',
              showCancelButton: false,
              confirmButtonText: '确定',
            })
            this.formData.cardNo = null
          }
        })
      },
    },
  }
</script>

<style scoped>
  .rbutton {
    height: 60px;
    background-color: #8a8a8a;
    color: #F0F0F0;
    font-size: 18px;
    font-weight: 600;
    padding: 10px;
  }

  .demo-drawer-footer {
    width: 100%;
    position: absolute;
    bottom: 0;
    left: 0;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    background: #fff;
  }

  .ivu-table td.no_padding > .ivu-table-cell {
    padding-left: 0;
    padding-right: 0;
  }
</style>
