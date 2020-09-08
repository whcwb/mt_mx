<template>
  <div class="box_col">
    <Menu mode="horizontal" :theme="theme1" :active-name="activeName" ref="activeName"
          style="font-size: 48px;font-weight: bold;margin-bottom: 8px" @on-select="selectKc">
      <Menu-item v-for="item in JGList" :value="item.jgdm" :name="item.jgdm">
        {{ item.jgmc }}
      </Menu-item>
    </Menu>
    <Row style="margin-bottom: 8px" type="flex" align="bottom">
      <Col span="6" style="display: flex;align-items: center">
        <span
            style="cursor: pointer;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
            @click="toEmpty">余额归零</span>
      </Col>
      <Col span="18">
        <Row type="flex" justify="end" :gutter="8">
          <Col span="4">
            <Select filterable clearable v-model="param.jlJx" @on-change="getData" :label-in-value="true"
                    placeholder="请输入驾校">
              <Option v-for="(item,index) in JX" :key="index" :value="item.val">{{ item.val }}-{{ item.by1 }}</Option>
            </Select>
          </Col>
          <Col span="4">
            <Input size="large" v-model="param.jlXmLike" @on-keyup.enter="getData" clearable placeholder="请输入教练姓名"/>
          </Col>
          <Col span="1" align="center" style="margin-right: 16px">
            <Button type="primary" @click="exportExcel">
              <Icon type="md-download"></Icon>
              <!--查询-->
            </Button>
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
    <Modal
        title="添加教练"
        v-model="DrawerVal"
        :closable="false"
        width="800"
        :mask-closable="false">

      <Form :model="formData" label-position="top">
        <Row :gutter="32" style="display: flex;justify-content: space-between">
          <Col span="4">
            <FormItem label="教练员姓名" label-position="top" style="width: 95%">
              <Input v-model="formDataJL.jlXm"/>
            </FormItem>
          </Col>
          <Col span="5">
            <FormItem label="教练员联系方式" label-position="top" style="width: 95%">
              <Input v-model="formDataJL.jlLxdh"/>
            </FormItem>
          </Col>
          <Col span="6">
            <FormItem label="所属驾校" label-position="top" style="width: 95%">
              <Select filterable clearable v-model="formDataJL.jlJx" :label-in-value="true">
                <Option v-for="(item,index) in schoolList" :key="index" :value="item.val">{{item.val}}-{{item.by1}}
                </Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="5">
            <FormItem label="驾校" label-position="top" style="width: 95%;">
              <RadioGroup v-model="formDataJL.jlLx" size="small">
                <Radio label="00" @click="formDataJL.jlLx='00'"> 本校</Radio>
                <Radio label="10" @click="formDataJL.jlLx='10'">外校</Radio>
              </RadioGroup>
            </FormItem>
          </Col>
          <Col span="4">
            <FormItem v-if="formDataJL.jlLx=='00'" label="队号" label-position="top" style="width: 95%">
              <Select v-model="formDataJL.dh" @on-change="selectDh" label-in-value>
                <Option v-for="item in dhs" :value="item.dh">{{ item.dm }}</Option>
              </Select>
            </FormItem>
            <div v-else style="min-height: 1px"></div>
          </Col>
        </Row>

      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">关闭</Button>
        <Button type="primary" @click="wxjlSave">添加</Button>
      </div>
    </Modal>
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
              </Col>
              <Col span="11">
                <FormItem label="卡号" label-position="top">
                  <span style="font-size: 15px">{{payItem.cardNo?payItem.cardNo:'/'}}</span>
                </FormItem>
              </Col>
            </Row>
            <Row style="display: flex;justify-content: space-between">
              <Col span="6" style="padding-left: 5px">
                <FormItem label="实收金额" label-position="top">
                  <Input v-model="payItem.sfje" @on-change="changeJe"/>
                </FormItem>
              </Col>
              <Col span="2">
                <div></div>
              </Col>
              <Col span="6">
                <FormItem label="赠送比例" label-position="top">
                  <Select v-model="bl" @on-change="changeJe">
                    <Option v-for="(item,index) in blList" :value="item.val" :key="item.val">
                      {{item.val}}
                    </Option>
                  </Select>
                </FormItem>
              </Col>
              <Col span="2">
                <div></div>
              </Col>
              <Col span="6" style="padding-right: 5px">
                <FormItem label="赠送额度" label-position="top">
                  <Input type="number" v-model="zsje" readonly/>
                </FormItem>
              </Col>
            </Row>
            <Row style="display: flex;justify-content: center">
              <Col span="6" style="padding-left: 20px">
                <FormItem label="账户余额" label-position="top">
                  <Input v-model="zhye" readonly/>
                </FormItem>
              </Col>
              <Col span="18">
                <div></div>
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
            <Row style="display: flex;justify-content: space-between;">
              <Col span="11">
                <FormItem label="队号" labelposition="top" v-if="infoItem.jlLx=='00'">
                  <Select v-model="infoItem.dh" @on-change="selectDhInfo" label-in-value>
                    <Option v-for="item in dhs" :value="item.dh">{{ item.dm }}</Option>
                  </Select>
                </FormItem>
              </Col>
              <Col span="11">
              </Col>
            </Row>
          </Col>
        </Row>

      </Form>

      <div slot='footer'>
        <Button style="margin-right: 8px" @click="closeInfo">取消</Button>
        <Button type="error" style="margin-right: 8px" @click="delInfo">删除</Button>
        <Button type="primary" @click="updateInfo">确定</Button>
      </div>
    </Modal>

    <mxb :itemObj="jlItem" :isMxb="isMxb" :lx="lx" @closemxb="closeMXB"></mxb>
    <component :is="componentName" @closeEmpty="closeEmpty" :QRmodal="componentName==='empty'" :printClose="printClose"
               :hisPrintMess="hisPrintMess" :jgdm="param.jgdmLike"></component>
  </div>
</template>

<script>
import dhList from '../../../data/dhList'
import addjl from '../../lcsf/comp/addJL'
import mxb from '../jlcz/mxb'
import empty from '../jlcz/empty'
import {mapMutations} from 'vuex'
import printSignUp from '../../lcsf/comp/printSignUp'
import Cookies from 'js-cookie'

export default {
  name: "index",
  components: {
    addjl,
    mxb,
    empty,
    printSignUp
  },
  data() {
    return {
      QRmodal: false,
      activeName: '',
      JGList: [],
      theme1: 'light',
      dhs: dhList.dhs,
      bl: '',
      zhye: 0,
      blList: [],
      zsje: null,
      dataList: [],
      isMxb: false,
      lx: '',
      jlItem: {},
      pay: false,        //充值modal
      payItem: {
          je: null,
          sfje: null,
          id: ''
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
          jlLx: '00',
          dh: '',
          dm: ''
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
          orderBy: 'cjsj desc',
          jgdmLike: ''
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
            title: '队号',
            align: 'center',
            render: (h, p) => {
              if (p.row.dm) {
                return h('div', p.row.dm)
              } else {
                return h('div', '-')
              }
            }
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
              var _self = this.$options.parent
              if (value[0] == '1') {
                _self.param.cardNoIsNotNull = '1'
                _self.param.cardNoIsNull = ''
              } else if (value[0] == '0') {
                _self.param.cardNoIsNull = '1'
                _self.param.cardNoIsNotNull = ''
              } else {
                _self.param.cardNoIsNull = ''
                _self.param.cardNoIsNotNull = ''
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
                on: {
                  click: () => {
                    this.jlItem = p.row
                    this.lx = 'cz'
                    this.isMxb = true
                  }
                }
              }, p.row.cardJe + '元')
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
                on: {
                  click: () => {
                    this.jlItem = p.row
                    this.lx = 'kfr'
                    this.isMxb = true
                  }
                }
              }, p.row.ye + '元')
            },
            filters: [
              {
                label: '不为零',
                value: '1'
              }
            ],
            filterMultiple: false,
            filterRemote(value, row) {
              var _self = this.$options.parent
              if (value[0] == '1') {
                _self.param.yeGte='1'
              }else {
                _self.param.yeGte=''
              }
              _self.getData()
            },
          },
          {
            title: '操作',
            align: 'center',
            width: 160,
            fixed: "right",
            render: (h, p) => {
              let buttons = [];
              var v = this;
              buttons.push(
                h('Tooltip',
                  {props: {placement: 'top', content: '充值',}},
                  [
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0', borderRadius: '15px'},
                      on: {
                        click: () => {
                          this.payItem = JSON.parse(JSON.stringify(p.row))
                          this.zhye = this.payItem.cardJe
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
                      style: {margin: '0 10px 0 0', borderRadius: '15px'},
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
        if (n == false) {
          this.compName = ''
          this.formData = {}
          this.jlJx = ''
          this.je = 0
        }
      },
    },
    mounted() {
    },
    created() {
      this.getSchoolList();
      this.getJX();
      this.getCoachList();
      this.getBL();
      this.getJgs();
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
      exportExcel() {
        alert('aaaaa')
        let p = '';
        for (let k in this.param) {
          p += '&' + k + '=' + this.param[k];
        }
        p = p.substr(1);
        let accessToken = JSON.parse(Cookies.get('accessToken'));
        let token = accessToken.token;
        let userid = accessToken.userId;
        window.open(this.apis.url + '/api/lcwxjl/exportWxjl?token=' + token + "&userid=" + userid + "&" + p);
      },
      ...mapMutations([
        'set_LcTime',
        'Ch_LcTime'
      ]),
      changeJxLx() {
        this.formDataJL.dh = ''
        this.formDataJL.dm = ''
      },
      selectDhInfo(val) {
        this.infoItem.dh = val.value
        this.infoItem.dm = val.label
      },
      selectDh(val) {
        this.formDataJL.dh = val.value
        this.formDataJL.dm = val.label
      },
      selectKc(val) {
        this.param.jgdmLike = val
        this.getData();
      },
      getJgs() {
        this.$http.get("/api/lccl/getJgsByOrgCode").then(res => {
          this.JGList = res.result;
          this.param.jgdmLike = this.JGList[0].jgdm
          this.getData();
          this.activeName = this.JGList[0].jgdm
          this.$nextTick(() => {
            this.$refs.activeName.updateActiveName();
          })
        })
      },
      changeJe() {
        let je = 0;
        if (this.payItem.sfje) {
          je = parseInt(this.payItem.sfje);
        }

        this.zsje = Math.ceil(je * parseFloat(this.bl))
        this.zhye = parseInt(this.zsje) + je + parseInt(this.payItem.cardJe)
      },
      toEmpty() {
        this.componentName='empty'
      },
      closeEmpty(){
        this.componentName=''
        this.getData()
      },
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
        this.zsje = null;
        this.pay = false
      },
      winPrint(a) {
        this.hisPrintMess = a
        this.componentName = 'printSignUp'
      },
      savePay() {
        if (this.payItem.sfje == null) {
          this.swal({
            title: "请填写实付金额",
            type: 'warning'
          })
          return
        }
        if ((this.bl == "")) {
          this.swal({
            title: "请填写赠送比例",
            type: 'warning'
          })
          return
        }
        if (this.payItem.sfje > 0) {
          this.payItem.je = parseInt(this.zsje) + parseInt(this.payItem.sfje);
        }
        this.$http.post('/api/lcjl/cz', this.payItem).then(res => {
          if (res.code == 200) {
            this.pay = false;
            this.getData();
            this.payItem = {};
            this.zsje = null
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
        if (this.infoItem.jlLx == '10') {
          this.infoItem.dh = '';
          this.infoItem.dm = '';
        }
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
        if (this.formDataJL.jlLxdh == '') {
          this.swal({
            title: '请填写教练电话!',
            type: 'warning'
          })
          return
        }
        if (this.formDataJL.jlLx == '10') {
          this.formDataJL.dh = '';
          this.formDataJL.dm = '';
        }
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
          } else {
            this.swal({
              title: res.message,
              type: 'error'
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
        // this.$refs.jlySelect.clearSingleSelect();
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
        this.JX = this.dictUtil.getByCode(this, "ZDCLK1017");
      },
      getBL() {
        this.blList = this.dictUtil.getByCode(this, "ZDCLK1049");
        this.bl = this.blList[0].val;
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
