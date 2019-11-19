<template>
  <div class="box_col" style="position: relative">
    <Menu mode="horizontal" :active-name="activeName" @on-select="MenuClick">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          科三模训
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
        <Row type="flex" justify="end" :gutter="8">
          <!--          <Col  span="12" align="right" style="font-size: 24px;color: #2baee9">-->
          <!--            <div @click="compName='keyypd'"> 当前排队中-->
          <!--              <Button style="font-size: 20px;font-weight: 600" type="error">{{yyrs}}</Button>-->
          <!--            </div>-->
          <!--          </COl>-->
          <!--          <Col span="2" align="center">-->
          <!--            <Button type="success" style="border-radius: 35px;font-size: 20px" @click="yyClick">预</Button>-->
          <!--          </Col>-->
          <!--          <Col span="2" align="center">-->
          <!--            <Button type="error" style="border-radius: 35px;font-size: 20px" @click="faCar">发</Button>-->
          <!--          </Col>-->
          <!--          <Col span="2" align="center">-->
          <!--            <Button size="large" style="border-radius: 35px;font-size: 20px" type="warning" @click="giveCar.overCar(v,'2'),printClose=true">-->
          <!--              还-->
          <!--            </Button>-->
          <!--          </Col>-->
          <!--          <Col span="3">-->
          <!--            <Input size="large" v-model="formData.clBh" clearable  placeholder="请输入车辆编号"/>-->
          <!--          </Col>-->
          <!--          <Col span="3">-->
          <!--            <Select v-model="formData.clCx" clearable @on-change="getCarList">-->
          <!--              <Option  v-for="item in dictUtil.getByCode(v,'ZDCLK0040')" :value="item.key" :key="item.index">{{ item.val }}</Option>-->
          <!--            </Select>-->
          <!--          </Col>-->
          <!--          <Col span="3">-->
          <!--            <Select v-model="formData.clZt" clearable @on-change="getCarList">-->
          <!--              <Option v-if="item.key!='02' && item.key!='03'" v-for="item in dictUtil.getByCode(v,'ZDCLK1044')" :value="item.key" :key="item.index">{{ item.val }}</Option>-->
          <!--            </Select>-->
          <!--          </Col>-->
          <!--          <Col span="1" align="center">-->
          <!--            <Button type="primary" @click="getCarList">-->
          <!--              <Icon type="md-search"></Icon>-->
          <!--              &lt;!&ndash;查询&ndash;&gt;-->
          <!--            </Button>-->
          <!--          </Col>-->
          <!--        <pager-tit title="科二模训" style="float: left"></pager-tit>-->
          <div style="float: left;margin-top: 8px;cursor: pointer;margin-right: 12px">
    <span
      style="width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
      @click="formData.clZt = '',getCarList()">总计{{carList.length}}台</span>
            <span
              style="width: 60px;height: 80px;cursor: pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 10px;"
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

        </Row>
      </Col>
    </Row>

    <Row v-show="activeName=='1'">
      <Table ref="table" size="small" :columns="columns1" :data="carList" :highlight-row="true"></Table>
    </Row>

    <div class="boxbackborder box_col" v-if="activeName=='2'">
      <Row type="flex" justify="end" :gutter="8" style="margin:8px 0;">
        <!--        <Col span="6" style="padding: 10px 20px">-->
        <!--          <Button type="warning" @click="plzf">批量结算</Button>-->
        <!--        </Col>-->


        <!--<Col span="5" style="margin-right: -40px">-->
        <DatePicker v-model="dateRange.jssj"
                    style="margin-right: 5px"
                    @on-change="param.jssjInRange = v.util.dateRangeChange(dateRange.jssj)"
                    @on-open-change="pageSizeChange(param.pageSize)"
                    format="yyyy-MM-dd"
                    split-panels
                    type="daterange" :placeholder="'请输入时间'"></DatePicker>
        <!--</Col>-->
        <Col span="3">
          <Input size="large" v-model="param.clBh" clearable placeholder="请输入车辆编号"
                 @on-enter="pageSizeChange(param.pageSize)"/>
        </Col>
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
        <Col span="1" align="center" style="margin-right: 20px">
          <Button type="primary" @click="plzf">
            确认支付
          </Button>
        </Col>
      </Row>
      <Table :height="680" stripe
             size="small"
             @on-select="tabcheck"
             :columns="tableColumns" :data="pageData"></Table>
      <!--      <table-area :parent="v"></table-area>-->
      <Row class="margin-top-10 pageSty">
        <div style="text-align: right;padding: 6px 0">
          <Page :total=param.total
                :current=param.pageNum
                :page-size=param.pageSize
                :page-size-opts=[8,10,20,30,40,50]
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
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="it.zddm!='K2KF'">{{it.by9}}-{{it.zdmc}}元
                  </Option>
                </Select>
              </FormItem>
            </div>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm!=undefined&&formData.zddm.includes('K3AB')">
          <Col span="12">
            <FormItem :label="'人数'" label-position="top">
              <Input v-model="formData.xySl"></Input>
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px"  v-if="formData.zddm!=undefined && formData.zddm.includes('K3PY')">
          <Card>
            <p slot="title">学员信息</p>
            <p>
              <Row v-for="(item,index) in AMess" :key="index">
                <Col span="5" :class-name="'colsty'">
                  <Input type="text" size="default" v-model="item.xyXm" placeholder="学员姓名"/>
                </Col>
                <Col span="5" :class-name="'colsty'">
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
        <Row :gutter="32" style="padding-top: 5px" v-if="formData.lcKm == '3'">
          <Col span="12">
            <FormItem :label="'安全员'" label-position="top">
              <Select v-model="formData.zgId"  filterable ref="se" @on-query-change="searchJlyaq"

              >
                <Option v-for="(item) in sfaemanlist" :value="item.value" :key="item.value">{{item.label}}</Option>
              </Select>
            </FormItem>
          </Col>
        </Row>

        <!--        <radio-car v-if="carMess == null"-->
        <!--                   clKm="2"-->
        <!--                   @getCarItemMess="getCarItemMess"-->
        <!--        ></radio-car>-->

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
        <FormItem label="备注" label-position="top">
          <Input type="textarea" v-model="formData.bz" :rows="4"/>
        </FormItem>
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
      @on-ok="QRok"
      @on-cancel="QRcancel"
    >
      <div>
        <Row>
          <Col>
            <Table size="small" :columns="columns2" :data="QRmess.jls"></Table>
            <!--                        <Card>-->
            <!--                          <p slot="title" style="font-size: 20px;font-weight: 600">未支付订单</p>-->
            <!--                          <p v-for="(item,index) in QRmess.jls" :key="index" style="font-size: 18px;font-weight: 500;padding: 10px">{{item.clBh}}号车,时长{{item.sc}}分钟,费用{{item.lcFy}}元</p>-->
            <!--                        </Card>-->
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
              <p style="font-size: 18px;font-weight: 500;padding: 10px">
                <Checkbox disabled v-model="ls.ls3">{{ls.ls6}}</Checkbox>
                现金支付
              </p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">
                <Checkbox disabled v-model="ls.ls2">{{ls.ls6}}</Checkbox>
                充卡支付(余额:{{QRmess.cardje}}元)
              </p>
              <p style="font-size: 18px;font-weight: 500;padding: 10px">
                <Checkbox disabled v-model="ls.ls1">{{ls.ls6}}</Checkbox>
                抵扣支付(余额:{{QRmess.kfje}}元)
              </p>
            </Card>
          </Col>

        </Row>
        <Row style="text-align: left;padding-left: 10px">
          <p style="font-size: 20px;font-weight: 600;padding: 10px;color: red">{{QRmess.bz}}</p>
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
  import yydrawer from './yydrawer'
  import yyModel from './yyModel'
  import radioCar from '../comp/RadioCar'
  //还车
  import giveCar from '../comp/readCard'
  import {mapMutations} from 'vuex'
  import printNew from '../comp/printNew'

  export default {
    name: "index",
    components: {
      carCard, jlwh, addjl,
      print, radioCar, carStatistics,
      keyypd, yydrawer, yyModel,printNew
    },
    data() {
      return {
          Pmess: {},
          AMess: [
              {}
          ],
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
          // {
          //   type: 'index2', align: 'center', minWidth: 80,
          //   render: (h, params) => {
          //     return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
          //   }
          // },
            {type:'index',align: 'center', minWidth: 40,title:'序号'},
          {
            type: 'selection',
            width: 40,
            align: 'center'
          },
            {title: '驾校', key: 'jlJx', minWidth: 90,align: 'center', },
          {title: '教练员', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 90,align: 'center',},
            {
                title: '人数',
                key: 'xySl',
                minWidth: 60,
                align: 'center',
                render: (h, p) => {
                    if (p.row.xySl!=''&&p.row.xySl!=undefined){
                        return h('div', p.row.xySl+'人')
                    }else {
                        return ''
                    }

                }
            },
            {title: '车型', key: 'jlCx', minWidth: 60,align: 'center',},
            {
                title: '类型',
                minWidth: 120,
                align: 'center',
                render: (h, p) => {
                    if (p.row.zdxm != ''){
                        return h('div', p.row.zdxm.by9+' '+p.row.zdxm.zdmc)
                    }

                }
            },
          // {title: '车辆编号', key: 'clBh', searchKey: 'clBh', minWidth: 90,},
          // {
          //   title: '状态', minWidth: 120, render: (h, p) => {
          //     let s = '';
          //     if (!p.row.kssj || p.row.kssj === '') {
          //       s = '预约中'
          //     } else if ((p.row.kssj && p.row.kssj.length > 0) && (!p.row.jssj || p.row.jssj == '')) {
          //       s = '训练中'
          //     } else {
          //       s = '已结束'
          //     }
          //     return h('div', s);
          //   }
          // },

          {title: '开始时间', key: 'kssj', minWidth: 140,align: 'center',},
          {title: '结束时间', key: 'jssj', searchType: 'daterange', minWidth: 140,align: 'center',},
          {title: '时长', key: 'sc', minWidth: 80, defaul: '0',align: 'center',
              render:(h,p)=>{
              return h('div',p.row.sc+'分钟')
              }
          },
          // {title: '学员数量', key: 'xySl', minWidth: 90, defaul: '0'},
          // {title: '计费类型', key: 'lcLx',minWidth:90,dict:'ZDCLK1048'},
          {title: '费用', key: 'lcFy', append: '元', minWidth: 90, defaul: '0',align: 'center',
              render:(h,p)=>{
                  return h('div',p.row.lcFy+'元')
              }
          },
          {
            title: '订单状态', key: 'zfzt', minWidth: 80,align: 'center',
            render: (h, p) => {
              if (p.row.zfzt == '00') {
                return h('div', '未支付')
              } else {
                return h('div', '已支付')
              }
            }
          },
            {
                title: '安全员',
                minWidth: 100,
                align: 'center',
                render: (h, p) => {
                    return h('div', p.row.zgXm)
                }
            },
          {title: '凭证号', key: 'pz', minWidth: 150,align: 'center',},

          {
            title: '操作', minWidth: 60, fixed: 'right', align: 'center',render: (h, p) => {
              let buttons = [];
              buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '补打', () => {
                this.hisPrintMess = p.row
                this.componentName = 'printNew'
              }));
              // if(p.row.zfzt == '00'){
              //     buttons.push(this.util.buildButton(this, h, 'error', 'logo-yen', '结算', () => {
              //         this.$http.post('/api/lcjl/getBatchPay',{ids:p.row.id}).then((res)=>{
              //             if (res.code == 200){
              //                 let a = true
              //                 this.QRmess = res.result
              //                 this.QRmess.kssj = this.QRmess.kssj.substring(11,16)
              //                 this.QRmess.jssj = this.QRmess.jssj.substring(11,16)
              //                 if (this.QRmess.fdr.indexOf('1')!=-1){
              //                     this.ls.ls1 = true
              //                 }
              //                 if (this.QRmess.fdr.indexOf('2')!=-1){
              //                     this.ls.ls2 = true
              //                 }
              //                 if (this.QRmess.fdr.indexOf('3')!=-1){
              //                     this.ls.ls3 = true
              //                 }
              //                 this.QRmess.a = a
              //                 this.QRmodal = true
              //             }
              //         })
              //     }));
              // }

              return h('div', buttons);
            }
          },

        ],
        pageData: [],
        param: {
          orderBy: 'jssj desc',
          jssjIsNotNull: '1',
          total: 0,
          lcKm: 3,
          jssjInRange: '',
          zhLike: '',
          pageNum: 1,
          pageSize: 15,
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
        QRmodal: false,
        QRmess: {jls:[]},
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
        sfaemanlist: [],
        pyxyInfo:[      //培优学员信息

        ],
        formData: {
          zgId: '',
          xyZjhm: '',
          xyXm: '',
          xyDh: '',
          lcKm: '3',
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
          zddm: 'K3JS'
        },
        searchCoachList: [],
        loadingJly: false,
        loadingJlyaqy: false,
        yyrs: '0',
        bxJL: [],//本校
        wxJL: [],//外校
        jlJx: '',
        zxNum: 0,
        xxNum: 0,
        carList: [],
        coachList: [],
        param1: {
          notShowLoading: 'true',
          clKm: '3',
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
              minWidth: 100,
            render: (h, p) => {
                return h('div',{
                    style:{
                        // height:'30px',width:'30px',
                        fontSize: '16px',fontWeight:'600',
                        // backgroundColor:'#ffbb96',borderRadius:"25px",
                        // color:'#ffbb96',
                    }
                    }, p.row.clBh)
              // return h('Tag', {
              //   props: {
              //     type: 'volcano',
              //   },
              //   style:{
              //       font_size:'24px'
              //   }
              // }, p.row.clBh)
            }
          },
          // {
          //   title: '车牌号',
          //   key: 'clHm',
          //   align: 'center',
          //   width: 120,
          //   fixed: "left",
          // },
          // {
          //     title: '所属考场',
          //     key: 'clKc',
          //     align:'center',
          //     width:120
          // },
          {
            title: '车型',
            key: 'clCx',
            align: 'center',
              minWidth: 80,
            fixed: "left",
          },
          // {
          //   title: '车辆状态',
          //   key: 'clZt',
          //   align: 'center',
          //     minWidth: 150,
          //   fixed: "left",
          //   render: (h, p) => {
          //     if (p.row.clZt == '00') {
          //       return h('div', '空闲')
          //     } else {
          //       return h('div', '在训')
          //     }
          //   }
          // },
          {
            title: '操作',
            align: 'center',
              minWidth: 150,
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
                      ghost:true
                    },
                    style: {margin: '0 10px 0 0'},
                    on: {
                      click: () => {
                        this.formData.zddm = 'K3JS';
                        this.formData.lcClId = p.row.id
                        this.formData.lcKm = '3';
                        this.$http.post('/api/lcjl/Tc', {km: '3'}).then((res) => {
                          if (res.code == 200) {
                            this.fylist=[]
                            let fyArr = res.result
                            for (let r of fyArr) {
                              if (r.by8.includes(p.row.clCx)) {
                                r.editMode = false
                                r.zdmc = parseInt(r.zdmc)
                                r.by3 = parseFloat(r.by3)
                                r.by4 = parseFloat(r.by4)
                                this.fylist.push(r)
                              }
                            }
                          }
                        })
                        // console.log(p.row)
                        // console.log(ifCard)

                        // var ifCard = p.row.zdxm.by2 === '0' ? false : true
                        // if (ifCard) {
                        //   if (!!window.ActiveXObject || "ActiveXObject" in window) {
                        //
                        //   } else {
                        //     this.swal({
                        //       title: '已启用刷卡模式，请使用IE10以上的浏览器',
                        //       type: 'warning',
                        //       confirmButtonText: '关闭'
                        //     })
                        //     return
                        //   }
                        // }

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
                      type: 'warning',
                      size: 'small',
                        ghost:true
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
                                  // this.$Message.success(res.message)
                                  this.QRmess = res.result
                                  // this.QRmess.kssj = this.QRmess.kssj.substring(11, 16)
                                  // this.QRmess.jssj = this.QRmess.jssj.substring(11, 16)
                                  if (this.QRmess.fdr.indexOf('1') != -1) {
                                    this.ls.ls1 = true
                                  }
                                  if (this.QRmess.fdr.indexOf('2') != -1) {
                                    this.ls.ls2 = true
                                  }
                                  if (this.QRmess.fdr.indexOf('3') != -1) {
                                    this.ls.ls3 = true
                                  }
                                  if (p.row.lcJl.lcLx == '00') {
                                    this.QRmodal = true
                                  } else {
                                    this.print(res.result)
                                  }
                                  // this.print(res.result)
                                  this.getCarList()
                                    this.getSafemanList()
                                } else {
                                  this.swal({
                                    title: res.message,
                                    type: 'error'
                                  })
                                }
                              })
                            } else {
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
                                console.log(key, mess)
                                if (!key) {
                                  v.swal({
                                    title: mess,
                                    type: 'error',
                                    confirmButtonText: '确定',
                                  })
                                  // .then((res) => {
                                  // if (res.value) {
                                  //   v.showFQfzkp = false;
                                  //   v.save()
                                  // } else {
                                  //   v.showFQfzkp = false;
                                  //   v.showQfshowFQfzkpzkp = false;
                                  //   v.DrawerVal = false
                                  // }
                                  // })
                                } else {
                                  cardNo = mess;
                                  this.$http.post('/api/lcjl/updateJssj', {
                                    id: p.row.lcJl.id,
                                    cardNo: cardNo
                                  }).then((res) => {
                                    if (res.code == 200) {
                                      // this.$Message.success(res.message)
                                      this.QRmess = res.result
                                      this.QRmess.kssj = this.QRmess.kssj.substring(11, 16)
                                      this.QRmess.jssj = this.QRmess.jssj.substring(11, 16)
                                      if (this.QRmess.fdr.indexOf('1') != -1) {
                                        this.ls.ls1 = true
                                      }
                                      if (this.QRmess.fdr.indexOf('2') != -1) {
                                        this.ls.ls2 = true
                                      }
                                      if (this.QRmess.fdr.indexOf('3') != -1) {
                                        this.ls.ls3 = true
                                      }
                                      if (p.row.lcJl.lcLx == '00') {
                                        this.QRmodal = true
                                      } else {

                                      }
                                      // this.print(res.result)
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

              //
              // buttons.push(
              //     h('Tooltip',
              //         {props: {placement: 'top', content: '训练记录',}},
              //         [
              //             h('Button', {
              //                 props: {
              //                     type: 'warning',
              //                     size: 'small',
              //                 },
              //                 style: {margin: '0 10px 0 0'},
              //                 on: {
              //                     click: () => {
              //                        this.his(p.row)
              //                     }
              //                 }
              //             }, '记录')
              //         ]
              //     ),
              // );
              // buttons.push(
              //     h('Tooltip',
              //         {props: {placement: 'top', content: '打印票据',}},
              //         [
              //             h('Button', {
              //                 props: {
              //                     size: 'small',
              //                     icon:"ios-print"
              //                 },
              //                 style: {margin: '0 10px 0 0'},
              //                 on: {
              //                     click: () => {
              //                         if(p.row.lcJl == ''){
              //                             this.$http.post('/api/lcjl/getLatestJl',{clId:p.row.id}).then((res)=>{
              //                                 if (res.code == 200){
              //                                     if(res.result){
              //                                         this.print(res.result)
              //                                     }else {
              //                                         this.$Message.error('暂无练车记录！')
              //                                     }
              //
              //                                 }else {
              //                                     this.$Message.error(res.message)
              //                                 }
              //                             })
              //                         }else {
              //                             this.print(p.row.lcJl)
              //                         }
              //                     }
              //                 }
              //             }, '')
              //         ]
              //     ),
              // );
              return h('div', buttons);
            }
          },
          // {
          //     title: '安全员',
          //     key: 'zgXm',
          //     width:120,
          //     align:'center',
          //     render:(h,p)=>{
          //             return h('div',p.row.lcJl.zgXm)
          //     }
          // },
          {
            title: '教练员',
            key: 'jlXm',
              minWidth: 150,
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.lcJl.jlXm)
            }
          },
          // {
          //   title: '教练员电话',
          //   key: 'jlDh',
          //   align: 'center',
          //   render: (h, p) => {
          //     return h('div', p.row.lcJl.jlDh)
          //   }
          // },
          {
            title: '人数',
            key: 'xySl',
              minWidth: 100,
            align: 'center',
            render: (h, p) => {
                if (p.row.lcJl.xySl!=''&&p.row.lcJl.xySl!=undefined){
                    return h('div', p.row.lcJl.xySl+'人')
                }else {
                    return ''
                }

            }
          },
          {
            title: '开始时间',
              minWidth: 180,
            align: 'center',
            render: (h, p) => {
              if (p.row.lcJl != [] && p.row.lcJl.kssj != '')
                return h('div', p.row.lcJl.kssj.substring(11, 16))
            }
          },
          {
            title: '时长',
            key: 'sc',
            width: 150,
            align: 'center',
            render: (h, p) => {
              if (p.row.dqsc == '') {

              } else {
                return h('div', parseInt(p.row.dqsc / 60) + '分钟')
              }

            }
          },
          {
            title: '费用',
            align: 'center',
              minWidth: 120,
            render: (h, p) => {
              if (p.row.zj != '') {
                return h('div', p.row.zj + '元')
              }

            }
          },
            {
                title: '类型',
                minWidth: 180,
                align: 'center',
                render: (h, p) => {
                    if (p.row.zdxm != ''){
                        return h('div', p.row.zdxm.by9+' '+p.row.zdxm.zdmc)
                    }

                }
            },
            {
                title: '安全员',
                minWidth: 120,
                align: 'center',
                render: (h, p) => {
                        return h('div', p.row.lcJl.zgXm)
                }
            },


        ],
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
          // if (this.formData.lcClId == '') {
          //   this.showCAR = true
          // }
        }
      }
    },
    mounted() {
    },
    created() {
      this.getCoachList();
      this.getCarList();
      this.getzdlist();
      this.getSafemanList()
      // this.getYYdj();
      this.enter()
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
      ...mapMutations([
        'set_LcTime',
        'Ch_LcTime'
      ]),
        remove(i){
            this.AMess.splice(i,1)
        },
        pushmess() {
            let a = JSON.parse(JSON.stringify(this.Pmess));
            this.AMess.push(a);
        },
        getWXXY(AMess) {
            if (this.formData.zddm.indexOf('K3PY') == -1){
                return true
            }
             AMess = this.AMess
            let arrAMess = AMess.length - 1;
            let messarr = [];
            let dxarr = [];
            let sfzarr = [];
            let a = true
            console.log(AMess,'AMess');
            for (let i =0;i<AMess.length; i++){
                if(AMess[i].xyXm == undefined ||AMess[i].xyXm == ''||AMess[i].xyXm == null){
                    this.swal({
                        title: '请填写学员姓名',
                        type: 'error'
                    })
                    a = false
                    break
                }else {
                    messarr.push(AMess[i].xyXm)
                    dxarr.push(AMess[i].xyDh)
                    sfzarr.push(AMess[i].bz)
                    if (i == arrAMess) {
                        console.log(dxarr.join(','))
                        console.log(messarr.join(','))
                        this.formData.xyXm = messarr.join(',');
                        this.formData.xyDh = dxarr.join(',');
                        this.formData.xyZjhm = sfzarr.join(',');
                    }
                }

            }
            return a
            // AMess.forEach((item, index) => {
            //     console.log(item.xyXm);
            //
            // })
        },
        scXY(e){
            this.AMess=[{}];
            e = parseInt(e);
            for (let i =1;i<e;i++){
                this.AMess.push({})
            }
        },
      getSafemanList() {
        this.$http.post('/api/zgjbxx/getAqy', {notShowLoading: 'true'}).then((res) => {
          if (res.code == 200) {
            res.result.forEach((item, index) => {
              let py = this.util.parsePY(item.xm)
              item.label = item.xm + ' [' + py + ']'
              item.value = item.id
              if (index == res.result.length - 1) {
                  console.log("--------------")
                this.sfaemanlist = res.result
                  console.log(res.result + "====");
                  console.log(this.sfaemanlist[0].label + "+++++");
              }
            })
              this.formData.zgId = ''
              console.log(this.sfaemanlist);
            // this.$nextTick()
          } else {
            this.$Message.info(res.message);
          }
        })
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
            // this.QRmess.kssj = this.QRmess.kssj.substring(11, 16)
            // this.QRmess.jssj = this.QRmess.jssj.substring(11, 16)
            if (this.QRmess.fdr.indexOf('1') != -1) {
              this.ls.ls1 = true
            }
            if (this.QRmess.fdr.indexOf('2') != -1) {
              this.ls.ls2 = true
            }
            if (this.QRmess.fdr.indexOf('3') != -1) {
              this.ls.ls3 = true
            }
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
          console.log(r);
        }
        ids.push(row.id)
        let a = ids.join(',')
        this.qrids = a
      },
      MenuClick(name) {
        var v = this
        this.activeName = name;
        if (name == '1') {
          this.getCarList()
        } else if (name == '2') {
          this.dateRange.jssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59'];
          this.param.jssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59';
          v.param.pageSize = 10;
          console.log(this.param);
          v.util.getPageData(v)
        } else {

        }
      },
      tabClick(name) {
        var v = this
        if (name == '0') {
          this.getCarList()
        } else if (name == '1') {
          this.dateRange.jssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59'];
          this.param.jssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59';
          v.param.pageSize = 10;
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
          // r.sc = this.parseTime(r.sc)
          r.kssj = r.kssj.substring(0, 16)
          r.jssj = r.jssj.substring(0, 16)
        }
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
            title: '该教练当前所有记录都已结束!是否仍要稍后支付?',
            type: 'question',
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          }).then(p => {
            if (p.value) {

            } else {
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
                  // this.$Message.success(res.message)
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
              // this.$Message.success(res.message)
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
        console.log(v)
        console.log(this.formData.zddm);

        // var ifCard = false;
        // this.fylist.map((val, index, arr) => {
        //   if (val.zddm === v) {
        //     ifCard = val.by2 === '0' ? false : true
        //   }
        // })
        //
        // if (ifCard) {
        //   if (!!window.ActiveXObject || "ActiveXObject" in window) {
        //   } else {
        //     this.swal({
        //       title: '该套餐已启用刷卡模式，请使用IE10以上的浏览器',
        //       type: 'warning',
        //       confirmButtonText: '关闭'
        //     })
        //     return
        //   }
        // }

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
        searchJlyaq(query){
            console.log(query);
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
          this.searchCoachList = [];
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
          this.AMess=[{}];
          this.formData = {}
        this.DrawerVal = false

          this.$refs.re.clearSingleSelect();
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
            lcKm: '3',
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
            this.AF.carCard('3', mess, (type, res) => {
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
      print(mess) {//还车
        this.hisPrintMess = mess
        // setTimeout(()=>{
        //   this.$refs['backcar'].doPrint()
        // },1000)
        this.componentName = 'print'
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
        this.zxNum = 0;
        this.xxNum = 0;
        this.$http.post('/api/lccl/getCar', {
          notShowLoading: 'true',
          pagerNum: 1,
          pageSize: 99999,
          clKm: "3",
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
              this.coachList.push({label: r.jlJx + '_' + r.jlXm + ' [' + py + ']' + '_' + r.jlLxdh, value: r.id});
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
            ifCard = val.by2 === '0' ? false : true
          }
        })

        console.log(this.formData.cardNo, '455')

        if (!ifCard || (ifCard && (this.formData.cardNo != '' && this.formData.cardNo != undefined && this.formData.cardNo != null))) {
            //判断是否需要刷卡 by2 0不刷 1刷
           if ( this.getWXXY()){
               this.$http.post('/api/lcjl/save', this.formData).then(res => {
                   if (res.code == 200) {
                       this.DrawerVal = false;
                       this.getCarList();

                       this.formData = {zgId: ''};
                       this.getSafemanList()
                       this.AMess=[{}];
                       // this.swal({
                       //   title: '发车成功',
                       //   type: 'success',
                       //   confirmButtonText: '确定',
                       // })
                       this.carMess = null
                       this.$refs.re.clearSingleSelect();
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
           }

        }
        else {

          if (!!window.ActiveXObject || "ActiveXObject" in window) {
          } else {
            this.swal({
              title: '该套餐已启用刷卡模式，请使用IE10以上的浏览器',
              type: 'warning',
              confirmButtonText: '关闭'
            })
            return
          }
          var v = this
          this.giveCar.readCard((key, mess) => {

            console.log(key, mess)
            if (!key) {
              // if (this.DrawerVal) {
              //   let v = this
              //   setTimeout(() => {
              //     if (v.DrawerVal) {
              //       v.save()
              //     }
              //   }, 200)
              // }
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
                  v.save()
                } else {
                  v.showFQfzkp = false;
                  v.showQfshowFQfzkpzkp = false;
                  v.DrawerVal = false
                  v.formData.cardNo = ''
                }
              })
            } else {
              // this.AF.carCard('2', mess, (type, res) => {
              //   console.log('**********', res);
              this.formData.cardNo = mess;
              this.save()
              // if (type) {
              //   if (res.result) {
              //     //如果车辆已经绑卡   返回车辆信息
              //     v.carMess = res.result
              //     this.formData.lcClId = v.carMess.id
              //   }
              //   this.DrawerVal = true;
              //   v.showFQfzkp = false;
              //   this.formData.cardNo = mess;
              //   v.save()
              // } else {
              //   this.DrawerVal = false;
              //   return
              // }
              // })
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
</style>
