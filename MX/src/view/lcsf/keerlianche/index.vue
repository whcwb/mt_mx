<template>
  <div class="box_col" style="position: relative">
    <Menu mode="horizontal" :active-name="activeName" @on-select="MenuClick">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          科二模训
        </div>
      </MenuItem>
      <MenuItem name="2">
        <div style="font-weight: 700;font-size: 16px">
          模训记录
        </div>
      </MenuItem>
    </Menu>
    <Row type="flex" style="padding: 10px 0" v-if="activeName=='1'">

      <Col span="24">
        <Row type="flex" style="justify-content: space-between;align-items: center" :gutter="8">
          <div style="display: flex;align-items: center">
            <span
              style="cursor:pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
              @click="yjChange">{{yj?'停止预警':'开启预警'}}</span>
            <span v-if="yj" style="margin-left: 10px" :class="yjclass">
              {{yjnr==''||yjnr=='操作成功'?'预警中......':yjnr+'号车即将超时，请提醒......'}}
            </span>
          </div>
          <div>
            <div style="float: left;margin-top: 8px;cursor: pointer;margin-right: 12px">
    <span
      style="width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
      @click="formData.clZt = '',getCarList()">总计{{zj}}台</span>
              <span
                style="width: 60px;height: 80px;cursor: pointer;border:1px solid #30bff5;color:black;padding:6px; border-radius: 4px;margin-left: 16px;"
                @click="formData.clZt = '01',getCarList()">
    在训{{zxNum}}台</span>
              <span
                style="width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 10px;cursor: pointer;"
                @click="formData.clZt = '00',getCarList()"
              >空闲{{xxNum}}台</span>
            </div>

            <Button type="primary" @click="getCarList" style="margin-right: 10px">
              <Icon type="md-refresh"/>
              <!--查询-->
            </Button>
          </div>
        </Row>
      </Col>
    </Row>

    <Row v-show="activeName=='1'">
      <Table ref="table" :height="AF.getPageHeight()-210" size="small" :columns="columns1" :data="carList"></Table>
    </Row>

    <div class="boxbackborder box_col" v-if="activeName=='2'">
      <Row type="flex" justify="end" :gutter="8" style="margin:8px 0;">
        <DatePicker v-model="dateRange.jssj"
                    style="margin-right: 5px"
                    @on-change="param.jssjInRange = v.util.dateRangeChange(dateRange.jssj)"
                    @on-open-change="pageSizeChange(param.pageSize)"
                    format="yyyy-MM-dd"
                    split-panels
                    :options="options2"
                    type="daterange" :placeholder="'请输入时间'"></DatePicker>
        <Col span="3">
          <Input size="large" v-model="param.jlXmLike" clearable placeholder="请输入教练姓名"
                 @on-enter="pageSizeChange(param.pageSize)"/>
        </Col>
        <Col span="1" align="center">
          <Button type="primary" @click="pageSizeChange(param.pageSize)">
            <Icon type="md-search"></Icon>
            <!--查询-->
          </Button>
        </Col>
        <Col span="1" align="center" style="margin-right: 30px">
          <Button type="primary" @click="plzf">
            确认支付
          </Button>
        </Col>
      </Row>
      <Table :height="AF.getPageHeight()-240"
             size="small"
             @on-select="tabcheck"
             @on-select-cancel="tabcheck"
             @on-select-all="tabcheck"
             @on-select-all-cancel="tabcheck"
             :columns="tableColumns" :data="pageData"></Table>
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


    <Modal
      title="分配车辆"
      v-model="DrawerVal"
      :closable="false"
      width="720"
      :mask-closable="false">
      <div slot="header">
        <div class="box_row">
          <div v-if="carMess">
            <Tag type="border" style="font-size: 24px;font-weight:bold;padding: 5px;height: 36px;" color="error">
              {{carMess.clBh}}-{{carMess.clCx}}
            </Tag>
          </div>
          <div style="font-size: 16px;margin-right: 28px;margin-top: 7px">
            <h2>开始训练</h2>
          </div>
        </div>
      </div>
      <Form :model="formData" label-position="top">
        <Row :gutter="32">
          <Col span="12">
            <div style="float: left">
              <FormItem label="教练员" style="width: 280px">
                <Select v-model="formData.jlId"
                        filterable
                        clearable
                        remote
                        loading
                        loading-text="请输入关键字搜索"
                        @on-query-change="searchJly"
                        ref="jlySelect"
                >
                  <Option v-for="(it,index) in searchCoachList" :value="it.value" :key="index">{{it.label}}</Option>
                </Select>
              </FormItem>
            </div>
            <div style="padding-top: 22px;">
              <Button type="primary" @click="compName ='addjl'">
                <Icon type="md-add"/>
              </Button>
            </div>
          </Col>

        </Row>
        <Row :gutter="32">
          <Col span="12">
            <div style="float: left">
              <FormItem label="计费套餐" label-position="top">
                <Select v-model="formData.zddm" style="width:200px" placeholder="计时500/小时" @on-change="lcFyChange">
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index">{{it.by9}}-{{it.zdmc}}元
                  </Option>
                </Select>
              </FormItem>
            </div>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2PY'">
          <Col span="12">
            <FormItem label="安全员" label-position="top">
              <Input v-model="formData.zgXm"/>
            </FormItem>
          </Col>
        </Row>

        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm =='K2JS-S'">
          <Col span="12">
            <FormItem label="人数" label-position="top" style="width:200px">
              <!--<InputNumber style="width: 100%" :max="1000" :min="1" :autofocus="true" v-model="formData.xySl"></InputNumber>-->
              <Input ref="inputRS" style="width: 100%" :autofocus="true" v-focus v-model="formData.xySl"></Input>
            </FormItem>
          </Col>
        </Row>
        <component :is="compName" :jxmc="jlJx"
                   @SaveOk="addjlSaveOk"
                   @colse="clearYY"
                   @remove="getCoachList('',true)"
                   @JLRowClick="JLRowClick"
                   @jxSeljxSel="(val)=>{getCoachList('',true)}"></component>

        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2PY'">
          <Col span="8">
            <FormItem label="学员姓名" label-position="top">
              <Input v-model="formData.xyXm"/>
            </FormItem>
          </Col>
          <Col span="8">
            <FormItem label="学员电话" label-position="top">
              <Input v-model="formData.xyDh"/>
            </FormItem>
          </Col>
          <Col span="8">
            <FormItem label="学员身份证号码" label-position="top">
              <Input v-model="formData.xyZjhm"/>
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">取消</Button>
        <Button type="primary" @click="save">发车</Button>
      </div>
    </Modal>
    <yyModel ref="yyModel"
             @close="close"
             @getCarList='getCarList'
    ></yyModel>
    <Modal
      title="是否立刻支付?"
      width="700px"
      v-model="QRmodal"
      :closable="false"
      :mask-closable="false"
      okText="确认支付"
      cancelText="稍后支付"
      @on-ok="QRokxj"
      @on-cancel="QRcancel"
    >
      <div>
        <Row>
          <Col>
            <Table size="small" :columns="columns2" :data="QRmess.jls"></Table>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <Card>
              <p slot="title" style="font-size: 20px;font-weight: 600">训练信息</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">教练员 : {{QRmess.jlXm}}</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">总时长 : {{QRmess.sc}}分钟</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px;color: red">总费用 : {{QRmess.lcFy}}元</p>
            </Card>
          </Col>
          <Col span="12">
            <Card>
              <p slot="title" style="font-size: 20px;font-weight: 600">支付方式</p>
              <RadioGroup v-model="QRmessxj.zf" vertical @on-change="getRs">
                <p style="font-size: 18px;font-weight: 500;padding: 10px">
                  <Radio label="1">
                    <Icon type="1"></Icon>
                    <span style="font-size: 18px;">现金支付</span>
                  </Radio>
                </p>
                <p style="font-size: 18px;font-weight: 500;padding: 10px">
                  <Radio label="2" :disabled="QRmess.cardje<=0">
                    <Icon type="social-android"></Icon>
                    <span style="font-size: 18px;">充卡支付(余额:{{QRmess.cardje}}元)</span>
                  </Radio>
                </p>
                <p style="font-size: 18px;font-weight: 500;padding: 10px;display: flex;align-items: center">
                  <Radio label="3" :disabled="QRmess.kfje<=0">
                    <Icon type="social-windows"></Icon>
                    <span style="font-size: 18px;">抵扣支付(余额:{{QRmess.kfje}}元)</span>
                  </Radio>
                  <Select v-if="QRmessxj.zf==='3'" v-model="QRmessxj.c" style="width:80px;display: inline-block"
                          size="small"
                          @on-change="getysxjA()">
                    <Option v-for="(item,index) in RS" :value="item" :key="index">{{item}}</Option>
                  </Select>
                </p>
              </RadioGroup>
            </Card>
          </Col>

        </Row>
        <Row style="text-align: left;padding-left: 10px">
          <p style="font-size: 20px;font-weight: 600;padding: 10px;color: red">应收现金{{this.ysxzA}}元</p>
        </Row>
      </div>
    </Modal>
    <Modal
      title="确认作废"
      width="700px"
      v-model="ZFmodal"
      :closable="false"
      :mask-closable="false"
      okText="作废"
      cancelText="取消"
      @on-ok="zf"
      @on-cancel=""
    >
      <div>
        <Row>
          <Col>
            <Table size="small" :columns="columns3" :data="ZFItem"></Table>
          </Col>
        </Row>
        <Row>
          <Col span="24">
            <Card>
              <p slot="title" style="font-size: 20px;font-weight: 600">训练信息</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">教练员 :
                {{ZFItem[0].jlXm===undefined?'':ZFItem[0].jlXm}}</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">总时长 :
                {{ZFItem[0].sc===undefined?'':ZFItem[0].sc}}分钟</p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px;color: red">总费用 : {{ZFItem[0].lcFy}}元</p>
            </Card>
          </Col>

        </Row>
        <Row style="text-align: left;padding-left: 10px">
        </Row>
      </div>
    </Modal>
    <component :is="componentName" :printClose="printClose" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>


<script>
  import carCard from '../comp/carCard'
  import jlwh from '../comp/jlWh'
  import addjl from '../comp/addJL'
  import carStatistics from '../statistics/carStatistics'
  import keyypd from '../comp/keyypd'
  import print from '../comp/print'
  import printNew from '../../../components/printNew'
  import yydrawer from './yydrawer'
  import yyModel from './yyModel'
  import radioCar from '../comp/RadioCar'
  //还车
  import giveCar from '../comp/readCard'
  import {mapMutations} from 'vuex'

  export default {
    name: "index",
    components: {
      carCard, jlwh, addjl, printNew,
      print, radioCar, carStatistics,
      keyypd, yydrawer, yyModel
    },
    data() {
      return {
        kfdj: 0,
        b: false,
        RS: [1, 2],
        tcIndex: 0,
        columns2: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: '车辆编号',
            key: 'clBh',
            align: 'center'
          },
          {
            title: '时长(分钟)',
            key: 'sc',
            align: 'center'
          },
          {
            title: '费用(元)',
            key: 'lcFy',
            align: 'center'
          },
          {
            title: '支付状态',
            key: 'zfzt',
            align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00' || p.row.jssj == '') {
                return h('Tag', {
                  props: {
                    type: 'volcano',
                  }
                }, '未支付')
              } else {
                return h('div', '已支付')
              }
            }
          },
          {
            title: '训练状态',
            key: 'clZt',
            align: 'center',
            render: (h, p) => {
              if (p.row.jssj == '') {
                return h('Tag', {
                  props: {
                    type: 'blue',
                  }
                }, '在训')
              } else {
                return h('div', '结束')
              }
            }
          }
        ],
        qrids: '',
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        searchBarButtons: [
          {title: '打印', click: 'print'}
        ],
        tableColumns: [
          {
            type: 'index2', minWidth: 60, align: 'center', title: '序号',
            // fixed: 'left',
            render: (h, params) => {
              return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
            }
          },
          {
            type: 'selection',
            align: 'center',
            minWidth: 50,
            // fixed: 'left',
          },
          {title: '驾校', align: 'center', key: 'jlJx', minWidth: 120},
          {title: '教练员', align: 'center', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 80},
          {
            title: '类型', align: 'center', minWidth: 140,
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
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              this.param.zddmLike = value;
              // var _self = this
              this.util.getPageData(this);
            },
            render: (h, p) => {
              if (p.row.zdxm != '') {
                return h('div', p.row.zdxm.by9 + ' ' + p.row.zdxm.zdmc)
              }
            }
          },
          {title: '人数', align: 'center', key: 'xySl', minWidth: 70, defaul: '0'},
          {title: '开始时间', align: 'center', key: 'kssj', minWidth: 145},
          {title: '结束时间', align: 'center', key: 'jssj', searchType: 'daterange', minWidth: 90,
            render: (h, p) => {
              return h('div', p.row.jssj.substring(10));
            }
          },
          {
            title: '时长', align: 'center', key: 'sc', minWidth: 100, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.sc + '分钟');
            }
          },
          {
            title: '应收', align: 'center', minWidth: 110, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元');
            }
          },
          {
            title: '实收', align: 'center', minWidth: 110, defaul: '0',
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
            title: '状态', key: 'zfzt', align: 'center', minWidth: 80,
            filters: [
              {
                label: '未支付',
                value: '00'
              },
              {
                label: '已支付',
                value: '10'
              },
              {
                label: '已作废',
                value: '20'
              },
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self = this
              console.log(_self.param);
              _self.param.zfzt = value;
              _self.util.getPageData(_self);

            },
            render: (h, p) => {
              if (p.row.zfzt == '00') {
                return h('div',
                  [
                    h('Button', {
                      props: {
                        type: 'warning',
                        size: 'small',
                        ghost: true,
                      },
                      style: {},
                      on: {
                        click: () => {
                          this.ZFmodal = true
                          this.ZFItem = []
                          this.ZFItem.push(p.row)
                        }
                      }
                    }, '未支付')
                  ])

                return h('div', '未支付')
              } else if (p.row.zfzt == '20') {
                return h('div',
                  [
                    h('Button', {
                      props: {
                        type: 'error',
                        size: 'small',
                        ghost: true,
                      },
                      style: {},
                    }, '已作废')
                  ])
              } else return h('div', '已支付')
            }
          },
          {
            title: '操作', minWidth: 70, align: 'center', render: (h, p) => {
              let buttons = [];
              if (p.row.zfzt == '10') {
                buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '补打', () => {
                  this.hisPrintMess = p.row
                  this.printClose = false
                  this.componentName = 'printNew'
                }));
              }
              return h('div', buttons);
            }
          },

        ],
        pageData: [],
        ifFinish: false,   //是否为结束训练，用于判断是否直接打印
        param: {
          orderBy: 'jssj desc',
          jssjIsNotNull: '1',
          total: 0,
          lcKm: 2,
          jssjInRange: '',
          zhLike: '',
          zfzt: '',
          pageNum: 1,
          pageSize: 15,
        },
        options2: {
          shortcuts: [
            {
              text: '一周',
              value() {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                return [start, end];
              }
            },
            {
              text: '一个月',
              value() {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                return [start, end];
              }
            },
            {
              text: '3个月',
              value() {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                return [start, end];
              }
            }
          ]
        },
        dateRange: {
          kssj: '',
          jssj: ''
        },
        activeName: '1',
        ls: {
          ls1: false,
          ls2: false,
          ls3: false,
        },
        ls1: {
          ls1: false,
          ls2: false,
          ls3: true,
        },
        QRmodal: false,
        QRmodalxj: false,
        QRmess: {},
        QRmessxj: {
          zf: '',
          c: ''
        },
        QRmessxjlist: [],
        fylist: [],
        giveCar: giveCar,
        v: this,
        DrawerVal: false,
        compName: '',
        componentName: '',
        printClose: false,
        hisPrintMess: '',
        clId: '',
        showFQfzkp: false,
        focus: false,
        formData: {
          xyZjhm: '',
          xyXm: '',
          xyDh: '',
          lcKm: '2',
          lcLx: '00',
          cardNo: '',
          clBh: '',
          lcClId: '',
          jlJx: '',
          jlId: "",
          zgXm: '',
          xySl: '',
          bz: '',
          lcFy: '',
          zddm: 'K2JS'
        },
        ZFmodal: false,
        ZFItem: [
          {
            jlXm: '',
            sc: ''
          }
        ],
        yjnr: '',
        yjYS:'',
        yjclass: '',
        yj: false,
        yjTimeOut: '',
        columns3: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: '车辆编号',
            key: 'clBh',
            align: 'center'
          },
          {
            title: '时长(分钟)',
            key: 'sc',
            align: 'center'
          },
          {
            title: '费用(元)',
            key: 'lcFy',
            align: 'center'
          },
          {
            title: '支付状态',
            key: 'zfzt',
            align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00' || p.row.jssj == '') {
                return h('Tag', {
                  props: {
                    type: 'volcano',
                  },
                }, '未支付')
              } else {
                return h('div', '已支付')
              }
            }
          },
          {
            title: '训练状态',
            key: 'clZt',
            align: 'center',
            render: (h, p) => {
              if (p.row.jssj == '') {
                return h('Tag', {
                  props: {
                    type: 'blue',
                  }
                }, '在训')
              } else {
                return h('div', '结束')
              }
            }
          }
        ],
        searchCoachList: [],
        loadingJly: false,
        yyrs: '0',
        bxJL: [],//本校
        wxJL: [],//外校
        jlJx: '',
        zxNum: 0,
        xxNum: 0,
        zj: 0,
        carList: [],
        coachList: [],
        param1: {
          notShowLoading: 'true',
          clKm: '2',
          clBh: '',
          orderBy: 'clZt asc,clBh asc,clCx asc'
        },
        showCAR: false,
        carMess: null,
        IntervalKE: setInterval(() => {
          this.Ch_LcTime()
        }, 1000),
        columns1: [

          {
            title: '车号',
            key: 'clBh',
            align: 'center',
            fixed: "left",
            width: 100,
            render: (h, p) => {
              return h('Tag', {
                props: {
                  type: 'volcano',
                }
              }, p.row.clBh)
            }
          },
          {
            title: '考场',
            key: 'clKc',
            align: 'center',
            width: 150,
            fixed: 'left',
            render: (h, p) => {
              return h('Tag', {
                props: {
                  type: 'white'
                }
              }, p.row.clKc)
            }
          },
          {
            title: '车型',
            key: 'clCx',
            align: 'center',
            width: 80,
            fixed: "left",
          },
          {
            title: '操作',
            align: 'center',
            width: 150,
            fixed: "left",
            render: (h, p) => {
              let buttons = [];
              var v = this;
              if (p.row.clZt == '00') {
                buttons.push(
                  h('Button', {
                    props: {
                      type: 'success',
                      size: 'small',
                    },
                    style: {margin: '0 10px 0 0'},
                    on: {
                      click: () => {
                        this.formData.zddm = 'K2JS';
                        this.formData.lcClId = p.row.id
                        this.formData.lcKm = '2';
                        this.$http.post('/api/lcjl/Tc', {km: '2', by5: '00'}).then((res) => {
                          if (res.code == 200) {
                            this.fylist = res.result
                            for (let r of this.fylist) {
                              r.editMode = false
                              r.zdmc = parseInt(r.zdmc)
                              r.by3 = parseFloat(r.by3)
                              r.by4 = parseFloat(r.by4)
                            }
                          }
                        })
                        this.DrawerVal = true;
                        this.showFQfzkp = false;

                      }
                    }
                  }, '开始训练')
                );
              }
              if (p.row.clZt == '01') {
                buttons.push(
                  h('Button', {
                    props: {
                      type: 'error',
                      size: 'small',
                    },
                    style: {margin: '0 10px 0 0'},
                    on: {
                      click: () => {
                        var v = this
                        this.swal({
                          title: '是否结束' + p.row.clBh + '号车(' + p.row.lcJl.jlXm + ")的训练？",
                          type: 'warning',
                          confirmButtonText: '结束',
                          cancelButtonText: '关闭',
                          showCancelButton: true
                        }).then((res) => {
                          if (res.value) {

                            var ifCard = p.row.zdxm.by2 === '0' ? false : true


                            var cardNo = ''
                            console.log(ifCard)
                            if (!ifCard) {

                              this.$http.post('/api/lcjl/updateJssj', {id: p.row.lcJl.id}).then((res) => {
                                if (res.code == 200) {
                                  this.QRmess = res.result
                                  var a = this.QRmess.kfje / this.QRmess.kfDj
                                  this.RS = []
                                  for (let i = 0; i < a; i++) {
                                    this.RS.push(i + 1)
                                  }
                                  this.QRmessxj.c = this.QRmess.kfje / this.QRmess.kfDj
                                  this.QRmessxj.zf = this.QRmess.fdr
                                  if (p.row.lcJl.lcLx == '00') {
                                    this.ifFinish = true
                                    this.QRmodal = true
                                  } else {
                                    this.print(res.result, true)
                                  }
                                  this.getCarList()
                                } else {
                                  this.swal({
                                    title: res.message,
                                    type: 'error'
                                  })
                                }
                              })
                            } else {
                              var v = this
                              this.giveCar.readCardChrome((key, mess) => {
                                console.log(key, mess)
                                if (mess === 'None') {
                                  v.swal({
                                    title: '请放置卡片结束！',
                                    type: 'error',
                                    confirmButtonText: '确定',
                                  })
                                } else {
                                  cardNo = mess;
                                  this.$http.post('/api/lcjl/updateJssj', {
                                    id: p.row.lcJl.id,
                                    cardNo: cardNo
                                  }).then((res) => {
                                    if (res.code == 200) {
                                      this.QRmess = res.result
                                      this.QRmess.kssj = this.QRmess.kssj.substring(11, 16)
                                      this.QRmess.jssj = this.QRmess.jssj.substring(11, 16)
                                      this.QRmessxj.zf = this.QRmess.fdr
                                      var a = this.QRmess.kfje / this.QRmess.kfDj
                                      this.RS = []
                                      for (let i = 0; i < a; i++) {
                                        this.RS.push(i + 1)
                                      }
                                      this.QRmessxj.c = a
                                      if (p.row.lcJl.lcLx == '00') {
                                        this.ifFinish = true
                                        this.QRmodal = true

                                      } else {

                                      }
                                      this.getCarList()
                                    } else {
                                      this.swal({
                                        title: res.message,
                                        type: 'error'
                                      })
                                    }
                                  })
                                }
                              })
                            }
                          } else {

                          }
                        })

                      }
                    }
                  }, '结束训练')
                );
              }

              return h('div', buttons);
            }
          },
          {
            title: '驾校',
            width: 150,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.lcJl.jlJx)
            }
          },
          {
            title: '教练员',
            key: 'jlXm',
            width: 150,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.lcJl.jlXm)
            }
          },
          {
            title: '开始时间',
            align: 'center',
            render: (h, p) => {
              if (p.row.lcJl != [] && p.row.lcJl.kssj != '')
                return h('div', p.row.lcJl.kssj.substring(11, 16))
            }
          },
          {
            title: '时长',
            key: 'sc',
            width: 120,
            align: 'center',
            render: (h, p) => {
              if (p.row.dqsc == '') {

              } else {
                return h('div', parseInt(p.row.dqsc) + '分钟')
              }

            }
          },
          {
            title: '费用',
            align: 'center',
            render: (h, p) => {
              if (p.row.zj != '') {
                return h('div', p.row.zj + '元')
              }

            }
          },


        ],
        ysxzA: '',
        kfje: 0,
        bfinfo: {}
      }
    },
    watch: {
      activeName: function (n, o) {
        this.MenuClick(n)
      },
      DrawerVal: function (n, o) {
        var v = this
        if (n == false) {
          this.compName = ''
          this.formData = {}
          this.jlJx = ''
        } else {
          this.formData.xySl = ''
        }
      },
      'QRmessxj.zf': function (n, o) {
        if (n == '3') {
          this.QRmessxj.c = this.QRmess.kfje / this.QRmess.kfDj
        }
        this.getysxjA()
      },
      'QRmessxj.c': function (n, o) {
        this.getysxjA()
      },
      QRmodal: function (n, o) {
        if (n == true)
          this.getysxjA()
      }
    },
    mounted() {
    },
    created() {
      this.getCoachList();
      this.getCarList();
      this.getzdlist();
      this.enter()
      window.clearInterval(this.yjTimeOut);

    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
      clearInterval(this.yjTimeOut);
      clearInterval(this.yjYS)
    },
    directives: {
      focus: function (el) {
        el.focus();
      }
    },
    methods: {
      ...mapMutations([
        'set_LcTime',
        'Ch_LcTime'
      ]),
      getKfdj(jlid) {
        this.$http.get("/api/lcjl/getKfDj", {jlId: jlid}).then(res => {
          if (res.code === 200) {
            this.QRmess.kfDj = res.result
          } else {
            this.$Message.error("数据异常, 请联系开发人员处理")
          }
        })
      },
      getysxjA() {
        if (this.QRmessxj.zf == '1') {
          this.ysxzA = this.QRmess.lcFy
          this.kfje = this.QRmess.kfje
          this.QRmessxj.c = 0
        } else if (this.QRmessxj.zf == '2') {
          this.ysxzA = (this.QRmess.lcFy - this.QRmess.cardje) > 0 ? (this.QRmess.lcFy - this.QRmess.cardje) : 0
          this.kfje = this.QRmess.kfje
          this.QRmessxj.c = 0
        } else {
          this.ysxzA = (this.QRmess.lcFy - (Math.ceil(500 / 60 * this.QRmess.dksc) * this.QRmessxj.c)) > 0 ? (this.QRmess.lcFy - (Math.ceil(500 / 60 * this.QRmess.dksc) * this.QRmessxj.c)) : 0
          this.kfje = this.QRmess.kfje - this.QRmess.kfDj * this.QRmessxj.c
        }
      },
      getRs(we) {
        if (we == '3') {
          var a = this.QRmess.kfje / this.QRmess.kfDj
          this.RS = []
          for (let i = 0; i < a; i++) {
            this.RS.push(i + 1)
          }
          this.QRmessxj.c = this.QRmess.kfje / this.QRmess.kfDj
        }
        this.getysxjA()
      },
      carYJ() {        //车辆预警
        this.$http.post('/api/lcjl/getCarEnd').then((res) => {
          if (res.code == 200) {
            this.yjnr = res.message
            var v = this
            clearInterval(this.yjYS)
              this.yjYS = setInterval(() => {
                if (v.yjclass == 'yjRed') {
                  v.yjclass = 'yjBlack'
                }
                else{
                  v.yjclass = 'yjRed'
                }
              }, 1000)

              setTimeout(() => {
                clearInterval(this.yjYS)
                this.yjnr=''
              }, 60000)
          } else {
            this.$Message.error(res.message)
          }
        })
      },
      xjzf(item) {
        item.zf = '1';
        this.QRmessxj = item;
        this.QRmessxjlist.push(item);
        this.QRmodalxj = true;
        this.kfje = this.QRmessxj.kfje;
        this.getysxjA()
      },
      enter() {
        var _this = this;
        document.onkeydown = function (e) {
          let key = window.event.keyCode;
          if (key == 13) {
            _this.getCarList();
          }
        };
      },
      pageChange(val) {
        this.param.pageNum = val
        this.util.getPageData(this)
      },
      pageSizeChange(val) {
        console.log(val);
        this.param.pageSize = val
        console.log(this.param.pageSize);
        this.param.pageNum = 1
        this.util.getPageData(this)

      },
      plzf() {
        this.$http.post('/api/lcjl/getBatchPay', {ids: this.qrids}).then((res) => {
          if (res.code == 200) {
            this.QRmess = res.result
            this.QRmessxj.zf = this.QRmess.fdr
            if (this.QRmessxj.zf == '3') {
              this.QRmessxj.c = this.QRmess.kfje / this.QRmess.kfDj
              const a = this.QRmessxj.c;
              this.RS = []
              for (let i = 0; i < a; i++) {
                this.RS.push(i + 1)
              }
              this.ysxzA = this.QRmess.lcFy
            }
            else {
              this.ysxzA = this.QRmess.lcFy
            }
            this.ifFinish = true
            this.QRmodal = true
          } else {
            this.$Message.error(res.message)
          }
        })
      },
      tabcheck(selection, row) {
        console.log(selection);
        console.log(row);
        let ids = []
        for (let r of selection) {
          ids.push(r.id)
        }
        this.qrids = ids.join(',')
        if (selection.length === 0) this.qrids = ''
      },
      MenuClick(name) {
        const v = this;
        this.activeName = name;
        if (name == '1') {
          this.getCarList()
        } else if (name == '2') {
          const end = new Date();
          const start = new Date();
          start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
          this.dateRange.jssj = [start, end]
          const d = start;
          const c = end;
          const datetimed = this.AF.trimDate(start) + ' ' + '00:00:00';
          const datetimec = this.AF.trimDate() + ' 23:59:59';
          this.param.jssjInRange = datetimed + ',' + datetimec
          v.param.pageSize = 15;
          v.util.getPageData(v)
        } else {

        }
      },
      yjChange() {
        this.yj = !this.yj
        window.clearInterval(this.yjTimeOut)
        if (this.yj) {
          var v = this
          v.carYJ()
          this.yjTimeOut = setInterval(function () {
              v.carYJ()
            },
            60000);
        } else {
          clearInterval(this.yjYS)
          window.clearInterval(this.yjTimeOut);
        }
      },
      tabClick(name) {
        var v = this
        if (name == '0') {
          this.getCarList()
        } else if (name == '1') {
          const end = new Date();
          const start = new Date();
          start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
          this.dateRange.jssj = [start, end]
          this.param.jssjInRange = start + ',' + end
          v.param.pageSize = 15;
          v.util.getPageData(v)
        } else {

        }
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
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)
        }
      },
      QRcancelxj() {
        this.QRmessxj = {};
        this.QRmessxjlist = [];
      },
      QRokxj() {
        this.$http.post('/api/lcjl/payCNY', {
          id: this.QRmess.id,
          zf: this.QRmessxj.zf,
          c: this.QRmessxj.c,
          dksc: this.QRmess.dksc,
          kfDj: this.QRmess.kfDj
        }).then((res) => {
          if (res.code == 200) {
            if (this.ifFinish)
              this.print(res.result, true)
            else this.print(res.result, false)
            this.QRmodal = false
            this.ysxzA = 0
            this.ifFinish = false
            this.util.getPageData(this)
            this.QRmessxj = {}
            this.QRmessxjlist = []
          } else {
            this.$Message.error(res.message)
          }
        })
      },
      zf() {
        this.swal({
          title: '确认作废该记录?',
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/lcjl/revokeJl', {id: this.ZFItem[0].id}).then((res) => {
              if (res.code == 200) {
                this.ZFmodal = false
                this.util.getPageData(this)
                this.$Message.info(res.message);
              } else {
                this.$Message.info(res.message);
              }
            })
          } else {

          }
        })
      },
      QRcancel() {
        var a = true
        if (this.QRmess.jls) {
          for (let r of this.QRmess.jls) {
            if (r.jssj == '') {
              a = false
              break
            }
          }
        }
        if (a) {
          this.swal({
            title: '该教练当前所有训练都已结束!是否仍要稍后支付?',
            type: 'question',
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          }).then(p => {
            console.log(p)
            if (p.value) {
              this.QRmess.id = ''
            } else {
              this.ysxzA = this.QRmess.lcFy
              this.QRmodal = true
            }
          })
        }
      },
      QRok() {
        if (this.QRmess.xjje == 0 && this.QRmess.fdr.indexOf("1") != -1) {
          // 如果此时不需要支付现金 并且是抵扣支付 则需要弹出是否继续确认支付
          this.swal({
            title: '开放日预存训练费(' + this.QRmess.kfje + ")元,需一次性使用完,是否强制结算!",
            type: 'question',
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          }).then(p => {
            if (p.value) {
              this.$http.post('/api/lcjl/batchPay', {ids: this.QRmess.id}).then((res) => {
                if (res.code == 200) {
                  this.QRmess.id = res.message
                  this.print(this.QRmess)
                  this.qrids = ''
                  this.util.getPageData(this)
                } else {
                  this.$Message.error(res.message)
                }
              })
            } else {
              this.QRmodal = true
            }
          })
        } else {
          this.$http.post('/api/lcjl/batchPay', {ids: this.QRmess.id}).then((res) => {
            if (res.code == 200) {
              this.QRmess.id = res.message
              this.print(this.QRmess)
              this.qrids = ''
              this.util.getPageData(this)
            } else {
              this.$Message.error(res.message)
            }
          })
        }

      },
      lcFyChange(v) {
        this.formData.zddm = v
        if (this.formData.zddm == 'K2JS-S') {
          this.$nextTick(() => {
            this.$refs['inputRS'].focus();
          })
        }
      },
      getzdlist() {
        let a = sessionStorage.getItem('dictMap')
        a = JSON.parse(a)
        this.fylist = a[67].zdxmList
        console.log(this.fylist);
      },
      getCarItemMess(it, index) {
        this.formData.lcClId = it.id
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
        }
      },
      clzt(zt) {

      },
      clearYY() {
        this.compName = '';
        this.getYYdj();
        this.getCarList();
        this.searchCoachList = [];
      },
      JLRowClick(row) {
        this.formData.jlId = row.id
      },
      close() {
        this.carMess = null;
        this.formData = {};
        this.XY = [];
        this.compName = '';
        this.DrawerVal = false;
        this.sfaemanlist = [];
        this.formData.lcLx = '00';
        this.searchCoachList = [];
        this.b = false
        //清空下拉框内容
        this.$refs.jlySelect.clearSingleSelect();
      },
      yyClick(val, cx) {
        console.log(val);
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
      faCar() {
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
              }
            })
          } else {
            this.AF.carCard('2', mess, (type, res) => {
              console.log('**********', res);
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
                return
              }
            })
          }
        })
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
      print(mess, ifPrint) {//还车     ifPrint:直接打印
        this.hisPrintMess = mess
        this.printClose = ifPrint ? true : false
        this.componentName = 'printNew'
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
      getCarList() {//获取车辆
        this.param1.clBh = this.formData.clBh
        this.$http.post('/api/lccl/getCar', {
          notShowLoading: 'true',
          pagerNum: 1,
          pageSize: 99999,
          clKm: "2",
          clBh: this.formData.clBh,
          orderBy: 'jgdm asc,clZt asc,clBh asc,clCx asc',
          clZt: this.formData.clZt,
          clCx: this.formData.clCx
        }).then((res) => {
          if (res.code == 200) {
            this.carList = res.page.list
            if (this.formData.clZt == '' || this.formData.clZt == undefined) {
              this.zxNum = this.xxNum =  0;
              this.zj =0
              for (let r of this.carList) {
                if (r.clZt === '01') {
                  this.zxNum++;
                } else if (r.clZt === '00') {
                  this.xxNum++;
                }
                this.zj++;
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
              this.coachList.push({
                label: r.jlJx + '_' + r.jlXm + ' [' + py + ']' + '_' + r.jlLxdh,
                value: r.id
              });
            }
          }
          if (res.code == 200 && res.result && id) {
            this.formData.jlId = id
          }
        })
      },
      save() {//发车
        this.formData.notShowLoading = 'true'

        var ifCard = false;
        this.fylist.map((val, index, arr) => {
          if (val.zddm === this.formData.zddm) {
            ifCard = val.by2 !== '0'
          }
        })

        console.log(this.formData.cardNo, '455')

        if (!ifCard || (ifCard && (this.formData.cardNo != '' && this.formData.cardNo != undefined && this.formData.cardNo != null))) {                //判断是否需要刷卡 by2 0不刷 1刷
          this.$http.post('/api/lcjl/save', this.formData).then(res => {
            if (res.code == 200) {
              this.DrawerVal = false;
              this.formData = {};
              this.getCarList();

              this.carMess = null
            } else {
              this.formData.cardNo = '';
              console.log(this.formData.cardNo)
              this.swal({
                title: res.message,
                type: 'warning'
              })
            }
          }).catch(err => {
          })
        } else {
          var v = this
          this.giveCar.readCardChrome((key, mess) => {

            console.log(mess)
            if (mess === 'None') {
              if (v.showFQfzkp) {
                return;
              }
              v.showFQfzkp = true;
              v.swal({
                title: '请放置卡片发车！',
                type: 'error',
                confirmButtonText: '发车',
                cancelButtonText: '取消',
                showCancelButton: true
              }).then((res) => {
                if (res.value) {
                  v.showFQfzkp = false;
                  v.save()
                } else {
                  v.showFQfzkp = false;
                  v.showQfshowFQfzkpzkp = false;
                  v.DrawerVal = false
                  v.formData.cardNo = ''
                }
              })
            } else {
              this.formData.cardNo = mess;
              this.save()
            }
          })

        }

      }
    },
  }
</script>

<style scoped>
  /deep/ .ivu-table-row-hover {
    background-color: #ff3226 !important
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

  .yjRed {
    color: red;
  }

  .yjBlack {
    color: black
  }

</style>
