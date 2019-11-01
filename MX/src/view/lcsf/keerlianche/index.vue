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
              <span style="width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;" @click="formData.clZt = '',getCarList()">总计{{carList.length}}台</span>
              <span style="width: 60px;height: 80px;cursor: pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 10px;"
              <span style="width: 60px;height: 80px;cursor: pointer;border:1px solid #30bff5;color:black;padding:6px; border-radius: 4px;margin-left: 16px;"
                    @click="formData.clZt = '01',getCarList()">
            在训{{zxNum}}台</span>
              <span style="width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 10px;cursor: pointer;"
                    @click="formData.clZt = '00',getCarList()"
              >空闲{{xxNum}}台</span>
            </div>

            <Button type="primary" @click="getCarList">
              <Icon type="md-refresh" />
              <!--查询-->
            </Button>

        </Row>
      </Col>
    </Row>

   <Row v-if="activeName=='1'">
     <Table ref="table" size="small" :columns="columns1" :data="carList" :highlight-row="true"></Table>
   </Row>

    <div class="boxbackborder box_col" v-if="activeName=='2'">
      <Row type="flex" justify="end" :gutter="8" style="margin-bottom:10px;">
<!--        <Col span="6" style="padding: 10px 20px">-->
<!--          <Button type="warning" @click="plzf">批量结算</Button>-->
<!--        </Col>-->


        <Col span="3">
          <DatePicker v-model="dateRange.jssj"
                      @on-change="param.jssjInRange = v.util.dateRangeChange(dateRange.jssj)"
                      @on-open-change="pageSizeChange(param.pageSize)"
                       format="yyyy-MM-dd"
                      split-panels
                      type="daterange" :placeholder="'请输入时间'"></DatePicker>
        </Col>
        <Col span="3">
          <Input size="large" v-model="param.clBh" clearable  placeholder="请输入车辆编号" @on-enter="pageSizeChange(param.pageSize)"/>
        </Col>
        <Col span="3">
          <Input size="large" v-model="param.jlXmLike" clearable  placeholder="请输入教练姓名"  @on-enter="pageSizeChange(param.pageSize)"/>
        </Col>
          <Col span="1" align="center">
            <Button type="primary" @click="pageSizeChange(param.pageSize)">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </Col>
      </Row>
      <Table :height="650" stripe
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
            <Tag type="border" style="font-size: 24px;font-weight:bold;padding: 5px;height: 36px;" color="error">{{carMess.clBh}}-{{carMess.clCx}}</Tag>
          </div>
          <div style="font-size: 16px;margin-right: 28px;margin-top: 7px">
            <h2>开始训练</h2>
          </div>
        </div>
      </div>
      <Form :model="formData" label-position="top">
        <Row :gutter="32">
          <Col span="12" >
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
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="it.zddm!='K2KF'">{{it.by9}}</Option>
                </Select>
                <!--              <CheckboxGroup v-model="formData.lcFy">-->
                <!--                <Checkbox label="900"></Checkbox>-->
                <!--              </CheckboxGroup>-->
              </FormItem>
            </div>
          </Col>
        </Row>
<!--        <Row :gutter="32" style="padding-top: 5px" v-if="formData.lcLx == '20'">-->
<!--          <Col span="12">-->
<!--            <FormItem label="计费套餐" label-position="top">-->
<!--              <Select v-model="formData.lcFy" style="width:200px">-->
<!--                <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="it.zddm!=''">{{it.zddm}}</Option>-->
<!--              </Select>-->
<!--              <CheckboxGroup v-model="formData.lcFy">-->
<!--                <Checkbox label="900"></Checkbox>-->
<!--              </CheckboxGroup>-->
<!--            </FormItem>-->
<!--          </Col>-->
<!--        </Row>-->
        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2PY'">
          <Col span="12">
            <FormItem label="安全员" label-position="top">
              <Input v-model="formData.zgXm"/>
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
             @getCarList = 'getCarList'
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
<!--            <Card>-->
<!--              <p slot="title" style="font-size: 20px;font-weight: 600">未支付订单</p>-->
<!--              <p v-for="(item,index) in QRmess.jls" :key="index" style="font-size: 18px;font-weight: 500;padding: 10px">{{item.clBh}}号车,时长{{item.sc}}分钟,费用{{item.lcFy}}元</p>-->
<!--            </Card>-->
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
          </Col> <Col span="12">
          <Card>
            <p slot="title" style="font-size: 20px;font-weight: 600">支付方式</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"> <Checkbox disabled v-model="ls.ls3">{{ls.ls6}}</Checkbox>现金支付;</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"><Checkbox disabled v-model="ls.ls2">{{ls.ls6}}</Checkbox>充卡支付(余额:{{QRmess.cardje}}元);</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"><Checkbox disabled v-model="ls.ls1">{{ls.ls6}}</Checkbox>抵扣支付(余额:{{QRmess.kfje}}元)</p>
          </Card>
          </Col>

        </Row>
        <Row style="text-align: left;padding-left: 10px">
          <p style="font-size: 20px;font-weight: 600;padding: 10px;color: red">{{QRmess.bz}} 元</p>
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
    export default {
        name: "index",
        components: {
            carCard, jlwh, addjl,
            print,radioCar, carStatistics,
            keyypd,yydrawer,yyModel},
        data() {
            return {
                columns2: [
                    {
                        title: '车辆编号',
                        key: 'clBh'
                    },
                    {
                        title: '时长(分钟)',
                        key: 'sc'
                    },
                    {
                        title: '费用(元)',
                        key: 'lcFy'
                    },
                    {
                        title: '支付状态',
                        key: 'zfzt',
                        render:(h,p)=>{
                            if (p.row.zfzt == '00' || p.row.jssj == ''){
                                return h('div','未支付')
                            }else {
                                return h('div','已支付')
                            }
                        }
                    },
                    {
                        title: '训练状态',
                        key: 'clZt',
                        render:(h,p)=>{
                            if (p.row.jssj == ''){
                                return h('div','在训')
                            }else {
                                return h('div','结束')
                            }
                        }
                    }
                ],
                qrids:'',
                apiRoot: this.apis.lcjl,
                choosedItem: null,
                searchBarButtons:[
                    {title:'打印',click:'print'}
                ],
                tableColumns: [
                    {
                        type: 'index2', align: 'center', minWidth: 80,
                        render: (h, params) => {
                            return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
                        }
                    },
                    // {
                    //     type: 'selection',
                    //     width: 60,
                    //     align: 'center'
                    // },
                    {title: '教练姓名', key: 'jlXm', searchKey: 'jlXmLike',minWidth:90},
                    {title: '车辆编号', key: 'clBh', searchKey: 'clBh',minWidth:90,},
                    {title: '状态', minWidth:120,render:(h,p)=>{
                            let s = '';
                            if (!p.row.kssj || p.row.kssj === ''){
                                s = '预约中'
                            }else if ((p.row.kssj && p.row.kssj.length > 0) && (!p.row.jssj || p.row.jssj == '')){
                                s = '训练中'
                            }else{
                                s = '已结束'
                            }
                            return h('div',s);
                        }},

                    {title: '开始时间', key: 'kssj',minWidth:140},
                    {title: '结束时间', key: 'jssj', searchType: 'daterange',minWidth:140},
                    {title: '时长(分钟)', key: 'sc',minWidth:80,defaul:'0'},
                    {title: '学员数量', key: 'xySl',minWidth:90,defaul:'0'},
                    // {title: '计费类型', key: 'lcLx',minWidth:90,dict:'ZDCLK1048'},
                    {title: '练车费用(元)', key: 'lcFy', append: '元',minWidth:90,defaul:'0'},
                    {title: '订单状态', key: 'zfzt',minWidth:80,
                        render:(h,p)=>{
                            if (p.row.zfzt == '00'){
                                return h('div','未支付')
                            }else {
                                return h('div','已支付')
                            }
                        }
                    },
                    {title:'操作',minWidth:140,render:(h,p)=>{
                            let buttons = [];
                            buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '补打', () => {
                                this.hisPrintMess = p.row
                                this.componentName = 'print'
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
                    orderBy:'jssj desc',
                    jssjIsNotNull:'1',
                    total: 0,
                    lcKm:2,
                    jssjInRange:'',
                    zhLike: '',
                    pageNum: 1,
                    pageSize:8,
                },
                dateRange: {
                    kssj: '',
                    jssj:''
                },
                activeName: '1',
                ls:{
                    ls1:false,
                    ls2:false,
                    ls3:false,
                },
                QRmodal:false,
                QRmess:{},
                fylist:[],
                giveCar:giveCar,
                v:this,
                DrawerVal: false,
                compName: '',
                componentName: '',
                printClose:false,
                hisPrintMess: '',
                clId: '',
                showFQfzkp:false,
                formData: {
                    xyZjhm: '',
                    xyXm: '',
                    xyDh: '',
                    lcKm:'2',
                    lcLx:'00',
                    cardNo:'',
                    clBh:'',
                    lcClId: '',
                    jlJx:'',
                    jlId:"",
                    zgXm:'',
                    xySl:'',
                    bz:'',
                    lcFy:'',
                    zddm:'K2JS'
                },
                searchCoachList: [],
                loadingJly:false,
                yyrs:'0',
                bxJL: [],//本校
                wxJL: [],//外校
                jlJx: '',
                zxNum:0,
                xxNum:0,
                carList: [],
                coachList:[],
                param1: {
                    notShowLoading:'true',
                    clKm: '2',
                    clBh: '',
                    orderBy:'clZt asc,clBh asc,clCx asc'
                },
                showCAR: false,
                carMess:null,
                IntervalKE: setInterval(() => {
                    this.Ch_LcTime()
                },  1000),
                columns1: [
                    {
                        title: '车辆编号',
                        key: 'clBh',
                        align:'center',
                        fixed: "left",
                        width:100,
                        render: (h, p) => {
                            return h('Tag', {
                                props: {
                                    type: 'volcano',
                                }
                            }, p.row.clBh)
                        }
                    },
                    {
                        title: '车牌号',
                        key: 'clHm',
                        align:'center',
                        width:120,
                        fixed: "left",
                    },
                    // {
                    //     title: '所属考场',
                    //     key: 'clKc',
                    //     align:'center',
                    //     width:120
                    // },
                    {
                        title: '车型',
                        key: 'clCx',
                        align:'center',
                        width:80,
                        fixed: "left",
                    },
                    {
                        title: '车辆状态',
                        key: 'clZt',
                        align:'center',
                        width:150,
                        fixed: "left",
                        render:(h,p)=>{
                            if (p.row.clZt == '00'){
                                return h('div','空闲')
                            }else {
                                return h('div','在训')
                            }
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
                        title: '教练员姓名',
                        key: 'jlXm',
                        width:150,
                        align:'center',
                        render:(h,p)=>{
                            return h('div',p.row.lcJl.jlXm)
                        }
                    },
                    {
                        title: '教练员电话',
                        key: 'jlDh',
                        align:'center',
                        render:(h,p)=>{
                            return h('div',p.row.lcJl.jlDh)
                        }
                    },
                    {
                        title: '学员数',
                        key: 'xySl',
                        width:100,
                        align:'center',
                        render:(h,p)=>{
                            return h('div',p.row.lcJl.xySl)
                        }
                    },
                    {
                        title: '开始时间',
                        align:'center',
                        render:(h,p)=>{
                            if(p.row.lcJl!=[] && p.row.lcJl.kssj!='')
                            return h('div',p.row.lcJl.kssj.substring(0,16))
                        }
                    },
                    {
                        title: '时长(分钟)',
                        key: 'sc',
                        width:120,
                        align:'center',
                        render:(h,p)=>{
                            if (p.row.dqsc ==''){

                            }else {
                                return h('div',parseInt(p.row.dqsc/60))
                            }

                        }
                    },
                    {
                        title: '当前费用(元)',
                        align:'center',
                        render:(h,p)=>{
                            if (p.row.zj!=''){
                                return h('div',p.row.zj)
                            }

                        }
                    },
                    {
                        title: '操作',
                        align:'center',
                        width: 150,
                        fixed: "right",
                        render: (h, p) => {
                            let buttons = [];
                            var v = this;
                            if (p.row.clZt == '00'){
                                buttons.push(
                                    h('Tooltip',
                                        {props: {placement: 'top', content: '开始训练',}},
                                        [
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
                                                        this.$http.post('/api/lcjl/Tc',{km:'2'}).then((res)=>{
                                                            if (res.code == 200){
                                                                this.fylist = res.result
                                                                for (let r of this.fylist){
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
                                            }, '开始')
                                        ]
                                    ),
                                );
                            }
                            if(p.row.clZt == '01'){
                                buttons.push(
                                    h('Tooltip',
                                        {props: {placement: 'top', content: '结束训练',}},
                                        [
                                            h('Button', {
                                                props: {
                                                    type: 'error',
                                                    size: 'small',
                                                },
                                                style: {margin: '0 10px 0 0'},
                                                on: {
                                                    click: () => {
                                                        this.swal({
                                                            title: '是否结束'+p.row.clBh+'号车('+p.row.lcJl.jlXm+")的训练？",
                                                            type: 'warning',
                                                            confirmButtonText: '结束',
                                                            cancelButtonText: '关闭',
                                                            showCancelButton: true
                                                        }).then((res) => {
                                                            if (res.value) {
                                                                this.$http.post('/api/lcjl/updateJssj', {id: p.row.lcJl.id}).then((res) => {
                                                                    if (res.code == 200) {
                                                                        // this.$Message.success(res.message)
                                                                        this.QRmess = res.result
                                                                        this.QRmess.kssj = this.QRmess.kssj.substring(11,16)
                                                                        this.QRmess.jssj = this.QRmess.jssj.substring(11,16)
                                                                        if (this.QRmess.fdr.indexOf('1')!=-1){
                                                                            this.ls.ls1 = true
                                                                        }
                                                                        if (this.QRmess.fdr.indexOf('2')!=-1){
                                                                            this.ls.ls2 = true
                                                                        }
                                                                        if (this.QRmess.fdr.indexOf('3')!=-1){
                                                                            this.ls.ls3 = true
                                                                        }
                                                                        if (p.row.lcJl.lcLx=='00'){
                                                                            this.QRmodal = true
                                                                        }else {

                                                                        }

                                                                        // this.print(res.result)
                                                                        this.getCarList()
                                                                    }else {
                                                                        this.$Message.error(res.message)
                                                                    }
                                                                })
                                                            } else {

                                                            }
                                                        })

                                                    }
                                                }
                                            }, '结束')
                                        ]
                                    ),
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
                    }
                ],
            }
        },
        watch: {
            DrawerVal: function (n, o) {
                var v = this
                if (n == false) {
                    this.compName = ''
                    this.formData = {}
                    this.jlJx= ''
                }else {
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
            this.getYYdj();
            this.enter()
        },
        beforeDestroy(){
            clearInterval(this.IntervalKE)
        },
        methods: {
            ...mapMutations([
                'set_LcTime',
                'Ch_LcTime'
            ]),
            enter(){
                var _this = this;
                document.onkeydown = function(e) {
                    let key = window.event.keyCode;
                    if (key == 13) {
                        _this.getCarList();
                    }
                };
            },
            pageChange(val){
                this.param.pageNum = val
                this.util.getPageData(this)
            },
            pageSizeChange(val){
                console.log(val);
                this.param.pageSize = val
                console.log(this.param.pageSize);
                this.param.pageNum = 1
                    this.util.getPageData(this)

            },
            plzf(){
                this.$http.post('/api/lcjl/getBatchPay',{ids:this.qrids}).then((res)=>{
                    if (res.code == 200){
                        this.QRmess = res.result
                        this.QRmess.kssj = this.QRmess.kssj.substring(11,16)
                        this.QRmess.jssj = this.QRmess.jssj.substring(11,16)
                        if (this.QRmess.fdr.indexOf('1')!=-1){
                            this.ls.ls1 = true
                        }
                        if (this.QRmess.fdr.indexOf('2')!=-1){
                            this.ls.ls2 = true
                        }
                        if (this.QRmess.fdr.indexOf('3')!=-1){
                            this.ls.ls3 = true
                        }
                        this.QRmodal = true
                    }else {
                        this.$Message.error(res.message)
                    }
                })
            },
            tabcheck(selection,row){
                console.log(selection);
                console.log(row);
                let ids = []
                for(let r of selection){
                    ids.push(r.id)
                    console.log(r);
                }
                ids.push(row.id)
                let a = ids.join(',')
                this.qrids = a
            },
            MenuClick(name) {
                this.activeName = name;
                if (name == '1') {
                    this.getCarList()
                } else if (name == '2') {
                    this.dateRange.jssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
                    this.param.jssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
                    this.param.pageSize = 20
                    console.log(this.param);
                    this.util.initTable(this);
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
            afterPager(list){
                for (let r of list){
                    r.sc = this.parseTime(r.sc)
                    r.kssj = r.kssj.substring(0,16)
                    r.jssj = r.jssj.substring(0,16)
                }
            },
            QRcancel(){

            },
            QRok(){
                    this.$http.post('/api/lcjl/batchPay',{ids:this.QRmess.id}).then((res)=>{
                        if (res.code == 200){
                            this.$Message.success(res.message)
                            this.QRmess.id = res.message
                            this.print(this.QRmess)
                            this.qrids=''
                            this.util.getPageData(this)
                        }else {
                            this.$Message.error(res.message)
                        }
                    })
            },
            lcFyChange(v){
                this.formData.zddm = v
                console.log(this.formData.zddm);
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
            searchJly (query) {
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
            clzt(zt){

            },
            clearYY(){
                this.compName='';
                this.getYYdj();
                this.getCarList();
                this.searchCoachList = [];
            },
            JLRowClick(row){
                this.formData.jlId = row.id
            },
            close() {
                // this.showCAR = false;
                this.carMess=null;
                this.formData = {};
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
                console.log(val);
                this.$refs.yyModel.show();
                this.formData.lcClId = val;
            },
            getYYdj(){
                this.$http.get('/api/lcjl/query',{params:{kssjIsNull:'1',orderBy:'cjsj asc',lcKm:'2',notShowLoading:'true'}}).then((res)=>{
                    if(res.code == 200){
                        if(res.result){
                            this.yyrs = res.result.length
                        }else{
                            this.yyrs = 0
                        }
                    }})
            },
            faCar(){
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
                this.giveCar.readCard((key,mess)=>{
                    if(!key){
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
                            title:mess,
                            type:'error',
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
                    }else {
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
            readkar(callback){
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
                this.giveCar.readCard((key,mess)=>{
                    if(!key){
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
                            title:mess,
                            type:'error',
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
                    }else {
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
                this.zxNum=0;
                this.xxNum=0;
                this.$http.post('/api/lccl/getCar', {notShowLoading:'true',pagerNum:1,pageSize:99999,clKm:"2",clBh:this.formData.clBh,orderBy:'clZt asc,clBh asc,clCx asc',clZt:this.formData.clZt,clCx:this.formData.clCx}).then((res) => {
                    if (res.code == 200) {
                        this.carList = res.page.list
                        for (let r of this.carList){
                            if (r.clZt === '01'){
                                this.zxNum ++;
                            }else if (r.clZt === '00'){
                                this.xxNum ++;
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
            getCoachList(id,clear) {
                if(clear){
                    this.formData.jlId = '';
                }
                this.coachList = [];
                this.$http.get('/api/lcwxjl/query',{params:{notShowLoading:'true'}}).then((res) => {
                    this.wxJL = res.result
                    if (res.code == 200 && res.result) {
                        for (let r of res.result) {
                            let py = this.util.parsePY(r.jlXm)
                            this.coachList.push({label: r.jlJx + '_' + r.jlXm +' ['+py+']'+ '_' + r.jlLxdh , value: r.id});
                        }
                    }
                    if(res.code == 200 && res.result && id ){
                        this.formData.jlId = id
                    }
                })
            },
            save(){//发车

                    this.formData.notShowLoading = 'true'
                    this.$http.post('/api/lcjl/save', this.formData).then(res => {
                        if (res.code == 200){
                            this.DrawerVal = false;
                            this.formData = {};
                            this.getCarList();
                            this.swal({
                                title:'发车成功',
                                type:'success',
                                confirmButtonText: '确定',
                            })
                            this.carMess=null
                        } else {
                            this.formData.cardNo = null;
                            this.swal({
                                title: res.message,
                                type: 'warning'
                            })
                        }
                    }).catch(err => {
                    })

            }
        },
    }
</script>

<style scoped>
  /deep/.ivu-table-row-hover{
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
