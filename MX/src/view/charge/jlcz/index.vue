<template>
  <div class="box_col">
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          开卡充值
        </div>
      </MenuItem>
    </Menu>
    <Row style="margin-bottom: 8px" type="flex" align="bottom">
      <Col span="6">
        <!--<pager-tit title="开卡充值" style="float: left"></pager-tit>-->
      </Col>
      <Col span="18">
        <Row type="flex" justify="end" :gutter="8">
          <Col span="4">
            <Select filterable clearable v-model="param.jlJx" @on-change="getData" :label-in-value="true" placeholder="请输入驾校">
              <Option v-for="(item,index) in JX" :key="index" :value="item.val">{{item.val}}-{{item.by1}}</Option>
            </Select>
          </Col>
          <!--<Col span="4">-->
            <!--<Input size="large" v-model="param.cardNo" @on-keyup.enter="getData" clearable placeholder="请输入卡号"/>-->
          <!--</Col>-->
          <Col span="4">
            <Input size="large" v-model="param.jlXmLike" @on-keyup.enter="getData" clearable placeholder="请输入教练姓名"/>
          </Col>
          <Col span="1" align="center" style="margin-right: 16px">
            <Button type="primary" @click="getData">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </Col>
          <Col span="1" align="center" style="margin-right: 10px">
            <Tooltip content="添加教练">
              <Button type="primary" @click="DrawerVal = true,compName ='addjl'">
                +
                <!--添加-->
              </Button>
            </Tooltip>
          </Col>
        </Row>
      </Col>
    </Row>

    <Row style="position: relative;">
      <Table stripe
             size="small"
             :height="AF.getPageHeight()-250"
             :columns="columns1"
             :data="dataList">
      </Table>
    </Row>
    <Row class="margin-top-10 pageSty">
      <div style="text-align: right;padding: 6px 0">
        <Page :total=totalS
              :current=param.pageNum
              :page-size=param.pageSize
              :page-size-opts=[8,10,15,20,30,40,50]
              show-total
              show-elevator
              show-sizer
              placement='top'
              @on-page-size-change='(n)=>{pageSizeChange(n)}'
              @on-change='(n)=>{pageChange(n)}'>
        </Page>
      </div>
    </Row>

    <!--<Row>-->
    <!--<Table ref="table" size="small" :columns="columns1" :data="carList"></Table>-->
    <!--</Row>-->
    <Modal
      title="添加教练"
      v-model="DrawerVal"
      :closable="false"
      width="720"
      :mask-closable="false">

      <Form :model="formData" label-position="top">
        <!--<Row :gutter="32">-->
        <!--<Col span="12">-->
        <!--<div style="float: left">-->
        <!--<FormItem label="教练员" style="width: 280px">-->
        <!--<Select v-model="formData.jlId"-->
        <!--filterable-->
        <!--clearable-->
        <!--remote-->
        <!--loading-->
        <!--loading-text="请输入关键字搜索"-->
        <!--@on-query-change="searchJly"-->
        <!--ref="jlySelect"-->
        <!--&gt;-->
        <!--<Option v-for="(it,index) in searchCoachList" :value="it.value" :key="index">{{it.label}}</Option>-->
        <!--</Select>-->
        <!--&lt;!&ndash;                <span style="color: red;font-size: 18px">*初始密码为123456</span>&ndash;&gt;-->
        <!--</FormItem>-->
        <!--</div>-->
        <!--<div style="padding-top: 22px;">-->
        <!--<Button type="primary" @click="compName ='addjl'">-->
        <!--<Icon type="md-add"/>-->
        <!--</Button>-->
        <!--</div>-->
        <!--</Col>-->

        <!--</Row>-->


        <!--<component :is="compName" :jxmc="jlJx"-->
                   <!--:showCloseBtn="false"-->
                   <!--@SaveOk="addjlSaveOk"-->
                   <!--@colse="clearYY"-->
                   <!--@remove="getCoachList('',true)"-->
                   <!--@JLRowClick="JLRowClick"-->
                   <!--@jxSeljxSel="(val)=>{getCoachList('',true)}"></component>-->

          <Row :gutter="32" style="display: flex;justify-content: space-between">
              <Col span="6">
                <FormItem label="教练员姓名" label-position="top" style="width: 95%">
                  <Input v-model="formDataJL.jlXm"/>
                </FormItem>
              </Col>
              <Col span="6">
                <FormItem label="教练员联系方式" label-position="top" style="width: 95%">
                  <Input v-model="formDataJL.jlLxdh"/>
                </FormItem>
              </Col>
              <Col span="6">
                <FormItem label="所属驾校" label-position="top" style="width: 95%">
                  <Select filterable clearable v-model="formDataJL.jlJx" :label-in-value="true" @on-change="jsSelect">
                    <Option v-for="(item,index) in schoolList" :key="index" :value="item.val">{{item.val}}-{{item.by1}}</Option>
                  </Select>
                </FormItem>
              </Col>
              <Col span="6">
                <FormItem label="驾校" label-position="top" style="width: 95%;">
                  <RadioGroup v-model="formDataJL.jlLx">
                    <Radio label="00"  @click="formDataJL.jlLx='00'"> 本校</Radio>
                    <Radio label="10" @click="formDataJL.jlLx='10'">外校</Radio>
                  </RadioGroup>
                </FormItem>
              </Col>
          </Row>

      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">关闭</Button>
        <Button type="primary" @click="wxjlSave">添加</Button>
      </div>
    </Modal>
    <!--<yyModel ref="yyModel"-->
    <!--@close="close"-->
    <!--@getCarList='getCarList'-->
    <!--&gt;</yyModel>-->
    <Modal
      title="充值"
      v-model="pay"
      :closable="false"
      width="720"
      :mask-closable="false">

      <Form :model="formData" label-position="top">
        <Row :gutter="32">
          <Col span="24">
            <Row style="display: flex;justify-content: space-between">
              <Col span="11">
                <FormItem label="姓名" label-position="top">
                  <span style="font-size: 15px">{{payItem.jlXm}}</span>
                </FormItem>
                <!--<span style="margin-right: 20px">持卡人</span>-->
                <!--<span style="font-size: 15px">{{payItem.jlXm}}</span>-->
              </Col>
              <Col span="11">
                <FormItem label="卡号" label-position="top">
                  <span style="font-size: 15px">{{payItem.cardNo?payItem.cardNo:'/'}}</span>
                </FormItem>
              </Col>
            </Row>
            <Row style="display: flex;justify-content: space-between">
              <Col span="11">
                <FormItem label="实收金额" label-position="top">
                  <Input v-model="payItem.sfje"/>
                </FormItem>
              </Col>
              <Col span="11">
                <FormItem label="充值金额" label-position="top">
                  <Input type="number" v-model="payItem.je" @on-change="je=Number(payItem.je)+Number(payItem.cardJe)"/>
                </FormItem>
              </Col>
            </Row>

            <!--<Row style="display: flex;justify-content: space-between">-->
            <!--<Col span="11">-->
            <!--<FormItem label="收款方式" label-position="bottom">-->
            <!--<Select v-model="formData.zddm" style="width:200px" @on-change="lcFyChange">-->
            <!--<Option value="">现金</Option>-->
            <!--<Option value="">支付宝</Option>-->
            <!--<Option value="">微信</Option>-->
            <!--</Select>-->
            <!--</FormItem>-->
            <!--</Col>-->
            <!--<Col span="23">-->
            <!--<FormItem label="付款人" label-position="top">-->
            <!--<Input v-model="formData.xyZjhm"/>-->
            <!--</FormItem>-->
            <!--</Col>-->
            <!--</Row>-->
            <Row style="display: flex;justify-content: center">
              <Col span="23">
                <FormItem label="备注" label-position="top">
                  <Input v-model="payItem.bz"/>
                </FormItem>
              </Col>
            </Row>
          </Col>

        </Row>

      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="closePay">取消</Button>
        <Button type="primary" @click="savePay">充值</Button>
      </div>
    </Modal>

    <Modal
      title="信息修改"
      v-model="info"
      :closable="false"
      width="720"
      :mask-closable="false">

      <Form label-position="top">
        <Row :gutter="32">
          <Col span="24">
            <Row style="display: flex;justify-content: space-between">
              <Col span="11">
                <FormItem label="教练姓名" label-position="top">
                  <Input v-model="infoItem.jlXm"/>
                </FormItem>
                <!--<span style="margin-right: 20px">持卡人</span>-->
                <!--<span style="font-size: 15px">{{payItem.jlXm}}</span>-->
              </Col>
              <Col span="11">
                <FormItem label="卡号" label-position="top">
                  <span style="font-size: 15px">{{infoItem.cardNo?infoItem.cardNo:'/'}}</span>
                </FormItem>
              </Col>
            </Row>
            <Row style="display: flex;justify-content: space-between">
              <Col span="11">
                <FormItem label="教练驾校" label-position="top">
                  <Select filterable clearable v-model="infoItem.jlJx" :label-in-value="true" placeholder="请输入驾校">
                    <Option v-for="item in JX" :key="item.val" :value="item.val">{{item.val}}-{{item.by1}}</Option>
                  </Select>
                  <RadioGroup v-model="infoItem.jlLx">
                    <Radio label="00" @click="infoItem.jlLx='00'"> 本校</Radio>
                    <Radio label="10" @click="infoItem.jlLx='10'">外校</Radio>
                  </RadioGroup>
                </FormItem>
              </Col>
              <Col span="11">
                <FormItem label="电话号码" label-position="top">
                  <Input v-model="infoItem.jlLxdh"/>
                </FormItem>
              </Col>
            </Row>

            <!--            <Row style="display: flex;justify-content: space-between">-->
            <!--              <Col span="11">-->
            <!--                <FormItem label="旧密码" label-position="top">-->
            <!--                  <Input v-model="infoItem.old"/>-->
            <!--                </FormItem>-->
            <!--              </Col>-->
            <!--              <Col span="11">-->
            <!--                <FormItem label="电话号码" label-position="top">-->
            <!--                  <Input v-model="infoItem.jlLxdh"/>-->
            <!--                </FormItem>-->
            <!--              </Col>-->
            <!--            </Row>-->

          </Col>

        </Row>

      </Form>

      <div slot='footer'>
        <Button style="margin-right: 8px" @click="closeInfo">取消</Button>
        <Button type="error" style="margin-right: 8px" @click="delInfo">删除</Button>
        <Button type="primary" @click="updateInfo">确定</Button>
      </div>
    </Modal>

    <!--    <Modal-->
    <!--      :title="passwordItem.jlXm+'修改密码'"-->
    <!--      v-model="password"-->
    <!--      :closable="false"-->
    <!--      width="720"-->
    <!--      :mask-closable="false">-->

    <!--      <Form :model="formData" label-position="top">-->
    <!--        <Row :gutter="32">-->
    <!--          <Col span="12">-->
    <!--            <div style="float: left">-->
    <!--              <FormItem label="原密码" label-position="top">-->
    <!--                <Input type="password" v-model="passwordItem.old"/>-->
    <!--              </FormItem>-->
    <!--              <FormItem label="新密码" label-position="top">-->
    <!--                <Input type="password" v-model="passwordItem.newPwd"/>-->
    <!--              </FormItem>-->
    <!--              <FormItem label="确认密码" label-position="top">-->
    <!--                <Input  type="password" v-model="passwordItem.newPwd1"/>-->
    <!--              </FormItem>-->
    <!--            </div>-->
    <!--          </Col>-->

    <!--        </Row>-->

    <!--      </Form>-->
    <!--      <div slot='footer'>-->
    <!--        <Button style="margin-right: 8px" @click="closePw">取消</Button>-->
    <!--        <Button type="primary" @click="updatePw">更改</Button>-->
    <!--      </div>-->
    <!--    </Modal>-->

    <mxb :itemObj="jlItem" :isMxb="isMxb" :lx="lx" @closemxb="closeMXB"></mxb>
    <component :is="componentName" :printClose="printClose" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  import addjl from '../../lcsf/comp/addJL'
  import mxb from '../jlcz/mxb'
  import {mapMutations} from 'vuex'
  import printSignUp from '../../lcsf/comp/printSignUp'

  export default {
    name: "index",
    components: {
      addjl,
      mxb,
      printSignUp
    },
    data() {
      return {
        dataList: [],
        isMxb: false,
        lx:'',
        jlItem: {},
        pay: false,        //充值modal
        payItem: {
          je: '',
          sfje: '',
          id: ''
          // no: ''
        },
        je: 0,   //充值金额+余额
        jxList: [],
        password: false,
        passwordItem: {},
        totalS: 0,
        info: false,
        infoItem: {},
        fylist: [],
        v: this,
        DrawerVal: false,
        formDataJL: {
          jlJx: '',
          jlXm: '',
          jlLxdh: '',
          jlLx:'00'
        },
        schoolList: [],
        compName: '',
        componentName: '',
        printClose: false,
        hisPrintMess: '',
        clId: '',
        showFQfzkp: false,
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
          pageNum: 1,
          pageSize: 15,
          jlJx: '',
          jlLx: '',
          orderBy:'cjsj desc'
        },
        showCAR: false,
        carMess: null,
        IntervalKE: setInterval(() => {
          this.Ch_LcTime()
        }, 1000),
        columns1: [
          {title: '序号', type: 'index', width: 60},
          {
            title: '驾校',
            key: 'jlJx',
            align: 'center',

          },
          {
            title: '教练员',
            key: 'jlXm',
            align: 'center',

          },
          {
            title: '教练员电话',
            key: 'jlLxdh',
            align: 'center',

          },
          {
            title: '本/外校',
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.jlLx == '00' ? '本校' : '外校');
            },
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
              var _self = this.$options.parent
              _self.param.jlLx = value[0] ? value[0] : ''
              _self.getData()
            },
          },
          {
            title: '卡号',
            key: 'cardNo',
            align: 'center',
            render: (h, p) => {
              return h('div', p.row.cardNo ? p.row.cardNo : '/');
            },
            filters: [
              {
                label: '已开卡',
                value: '1'
              },
              {
                label: '未开卡',
                value: '0'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self =  this.$options.parent
              if(value[0]=='1'){
                _self.param.cardNoIsNotNull='1'
                _self.param.cardNoIsNull=''
              }else if(value[0]=='0'){
                _self.param.cardNoIsNull='1'
                _self.param.cardNoIsNotNull=''
              }else{
                _self.param.cardNoIsNull=''
                _self.param.cardNoIsNotNull=''
              }
              _self.getData()
            },
          },
          {
            title: '充值卡余额',
            key: 'cardJe',
            align: 'center',
            render: (h, p) => {
              return h('Button', {
                props: {
                  type: 'error',
                  size: 'small',
                  ghost: true,
                },
                style: {},
                on:{
                  click: () =>{
                    this.jlItem = p.row
                    this.lx='cz'
                    this.isMxb = true
                  }
                }
              },p.row.cardJe + '元')
            }
          },
          {
            title: '开放日余额',
            key: 'ye',
            align: 'center',
            render: (h, p) => {
              return h('Button', {
                props: {
                  type: 'error',
                  size: 'small',
                  ghost: true,
                },
                style: {},
                on:{
                  click: () =>{
                    this.jlItem = p.row
                    this.lx='kfr'
                    this.isMxb = true
                  }
                }
              }, p.row.ye + '元')
            }
          },
          {
            title: '操作',
            align: 'center',
            width: 160,
            fixed: "right",
            render: (h, p) => {
              let buttons = [];
              var v = this;

              // buttons.push(
              //   h('Tooltip',
              //     {props: {placement: 'top', content: '明细',}},
              //     [
              //       h('Button', {
              //         props: {
              //           type: 'info',
              //           size: 'small',
              //         },
              //         style: {margin: '0 10px 0 0'},
              //         on: {
              //           click: () => {
              //             this.jlItem = p.row
              //             this.isMxb = true
              //           }
              //         }
              //       }, '明细')
              //     ]
              //   ),
              // );


              // buttons.push([
              //   h('Dropdown',
              //     {props: {trigger: "click"}},
              //     [
              //       h('Button', {
              //         props: {
              //           type: 'info',
              //           size: 'small',
              //         },
              //         style: {margin: '0 10px 0 0'},
              //         on: {
              //           click: () => {
              //           }
              //         }
              //       }, '操作'),
              //       h('DropdownMenu',
              //         {slot: "list"},
              //         [
              //           h('DropdownItem', {
              //               nativeOn: {
              //                 click(name) {
              //                   v.payItem = JSON.parse(JSON.stringify(p.row))
              //                   v.pay = true
              //                 }
              //               }
              //             }, '充值'
              //           ), h('DropdownItem', {
              //             nativeOn: {
              //               click(name) {
              //                 v.infoItem = JSON.parse(JSON.stringify(p.row))
              //                 v.info = true
              //               }
              //             }
              //           }, '维护'
              //         ),
              //           // h('DropdownItem', {
              //           //   nativeOn: {
              //           //     click() {
              //           //       v.password=true;
              //           //       v.passwordItem=p.row
              //           //     }
              //           //   }
              //           // }, '修改密码'),
              //           // h('DropdownItem', {
              //           //   nativeOn: {
              //           //     click(name) {
              //           //       v.resetPw(p.row)
              //           //     }
              //           //   }
              //           // }, '重置密码')
              //         ]
              //       )
              //     ])
              // ])

              buttons.push(
                h('Tooltip',
                  {props: {placement: 'top', content: '充值',}},
                  [
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0',borderRadius: '15px'},
                      on: {
                        click: () => {
                          this.payItem = JSON.parse(JSON.stringify(p.row))
                          this.pay = true
                        }
                      }
                    }, '充值')
                  ]
                ),
              );

              buttons.push(
                h('Tooltip',
                  {props: {placement: 'top', content: '维护',}},
                  [
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0',borderRadius: '15px'},
                      on: {
                        click: () => {
                          this.infoItem = JSON.parse(JSON.stringify(p.row))
                          this.info = true
                        }
                      }
                    }, '维护')
                  ]
                ),
              );
              return h('div', buttons);
            }
          }
        ],
        columns2: [
          {title: '#', type: 'index', width: 60},
          {
            title: '卡号',
            key: 'cardNo',
            align: 'center',
          },
          {
            title: '驾校',
            key: 'jlJx',
            align: 'center',

          },
          {
            title: '教练姓名',
            key: 'jlXm',
            align: 'center',

          },
          {
            title: '联系电话',
            key: 'jlLxdh',
            align: 'center',

          },
          {
            title: '余额',
            key: 'jlLxdh',
            align: 'center',

          }
        ],
        JX: [],
        printMess: {}
      }
    },
    watch: {
      DrawerVal: function (n, o) {
        var v = this
        if (n == false) {
          this.compName = ''
          this.formData = {}
          this.jlJx = ''
          this.je = 0
        } else {
          // if (this.formData.lcClId == '') {
          //   this.showCAR = true
          // }
        }
      },
      //
      // 'payItem.je': function (n, o) {
      //   console.log(n)
      //   this.je=n+Number(this.payItem.cardJe)
      // }
    },
    mounted() {
    },
    created() {

      // this.getCarList();
      // this.getzdlist();
      // this.getYYdj();
      this.getSchoolList()
      this.getData()
      this.getJX()
      this.getCoachList();
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
      ...mapMutations([
        'set_LcTime',
        'Ch_LcTime'
      ]),
      getData() {
        this.$http.get('/api/lcwxjl/pager', {
          params: this.param
        }).then(res => {
          if (res.code == 200) {
            this.totalS = res.page.total
            this.dataList = res.page.list
          } else {

          }
        }).catch(err => {
        })
      },
      closeMXB() {
        this.jlItem = {};
        this.isMxb = false
      },
      closePay() {
        this.payItem = {};
        this.pay = false
      },
      winPrint(a) {
        this.hisPrintMess = a
        this.componentName = 'printSignUp'
      },
      savePay() {
        this.$http.post('/api/lcjl/cz', this.payItem).then(res => {
          if (res.code == 200) {
            this.pay = false;
            this.getData();
            this.payItem = {};
            // this.swal({
            //   title: '充值成功',
            //   type: 'success',
            //   confirmButtonText: '确定',
            // })
            this.winPrint(res.result)
          } else {
            this.swal({
              title: res.message,
              type: 'warning'
            })
          }
        }).catch(err => {
        })
      },
      getSchoolList() {
        this.schoolList = [];
        let list = this.dictUtil.getByCode(this, 'ZDCLK1017')
        this.schoolList = list;
      },
      closeInfo() {
        this.info = false
        this.infoItem = {}
      },
      updateInfo() {
        this.$http.post('/api/lcwxjl/update', this.infoItem).then(res => {
          if (res.code == 200) {
            this.info = false
            this.infoItem = {}
            this.getData();
            this.swal({
              title: '更改成功',
              type: 'success',
              confirmButtonText: '确定',
            })
          } else {
            this.swal({
              title: res.message,
              type: 'warning'
            })
          }
        }).catch(err => {
        })
      },
      delInfo() {
        var v = this
        // this.swal({
        //   title: '确认删除' + this.infoItem.jlXm+'?',
        //   type: 'warning',
        //   confirmButtonText: '确认',
        //   cancelButtonText: '关闭',
        //   showCancelButton: true
        // }).then((res) => {
        //   if (res.value) {
        //     v.$http.post('/api/lcwxjl/unbindCardNo', {id:v.infoItem.id}).then(res => {
        //       if (res.code == 200) {
        //         v.info = false
        //         v.infoItem = {}
        //         v.getData();
        //         v.swal({
        //           title: '作废成功',
        //           type: 'success',
        //           confirmButtonText: '确定',
        //         })
        //       } else {
        //         v.swal({
        //           title: res.message,
        //           type: 'warning'
        //         })
        //       }
        //     }).catch(err => {
        //     })
        //   } else {
        //
        //   }
        // })
        this.swal({
          title: '确认删除' + this.infoItem.jlXm + '?',
          type: 'warning',
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          showCancelButton: true,
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/lcwxjl/remove/' + v.infoItem.id).then(res => {
              if (res.code == 200) {
                v.info = false
                v.infoItem = {}
                v.getData();
                v.swal({
                  title: '删除成功',
                  type: 'success',
                  confirmButtonText: '确定',
                })
              } else {
                this.swal({
                  title: res.message,
                  type: 'error'
                })
              }
            }).catch(err => {
            })
          }
        })
      },
      closePw() {
        this.password = false
        this.passwordItem = {}
      },
      updatePw() {
        var v = this
        this.swal({
          title: '确认更改' + v.passwordItem.jlXm + '的密码?',
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '关闭',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/lcwxjl/updatePwd', v.passwordItem).then((res) => {
              if (res.code == 200) {
                v.getData();
                v.passwordItem = {}
                v.password = false
                v.swal({
                  title: '修改成功',
                  type: 'success',
                  confirmButtonText: '确认',
                  cancelButtonText: '关闭',
                  showCancelButton: true
                })
              } else {
                v.$Message.error(res.message)
              }
            })
          } else {

          }
        })
      },
      resetPw(item) {
        this.swal({
          title: '确认重置' + item.jlXm + '的密码?',
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '关闭',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/lcwxjl/resetPwd', {cardNo: item.cardNo}).then((res) => {
              if (res.code == 200) {
                this.getData();
                this.swal({
                  title: '重置成功',
                  type: 'success',
                  confirmButtonText: '确认',
                  cancelButtonText: '关闭',
                  showCancelButton: true
                })
              } else {
                this.$Message.error(res.message)
              }
            })
          } else {

          }
        })
      },
      pageChange(val) {
        this.param.pageNum = val
        this.getData();
      },
      pageSizeChange(val) {
        this.param.pageSize = val
        this.getData();
      },
      lcFyChange(v) {
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
      wxjlSave() {
        if (this.formDataJL.jlJx == '') {
          this.swal({
            title: '请选择驾校!',
            type: 'warning'
          })
          return
        }
        if (this.formDataJL.jlXm == '') {
          this.swal({
            title: '请填写教练姓名!',
            type: 'warning'
          })
          return
        }
        if(this.formDataJL.jlLxdh == ''){
          this.swal({
            title:'请填写教练电话!',
            type:'warning'
          })
          return
        }
        console.log('######',this.formDataJL);
        let params = JSON.parse(JSON.stringify(this.formDataJL));
        this.$http.post('/api/lcwxjl/save', params).then(res => {
          if (res.code == 200) {
            this.swal({
              title: '新增完成',
              type: 'success'
            })
            this.formDataJL.jlXm = ''
            this.formDataJL.jlLxdh = ''
            this.DrawerVal = false;
            this.getData()
          }else {
            this.swal({
              title:res.message,
              type:'error'
            })
          }
        }).catch(err => {
        })
      },
      close() {
        // this.showCAR = false;
        this.carMess = null;
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
        this.$http.post('/api/lcwxjl/bindCardNo', {id: this.formData.jlId}).then(res => {
          if (res.code == 200) {
            this.DrawerVal = false;
            this.formData = {};
            this.getData();
            this.swal({
              title: '添加成功',
              type: 'success',
              confirmButtonText: '确定',
            })
          } else {
            this.formData.cardNo = null;
            this.swal({
              title: res.message,
              type: 'warning'
            })
          }
        }).catch(err => {
        })

      },
      getJX() {
        this.JX = this.dictUtil.getByCode(this, 'ZDCLK1017');
      }
    },
  }
</script>

<style scoped>
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
