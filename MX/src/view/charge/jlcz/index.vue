<template>
  <div class="box_col">
    <Row style="margin-bottom: 18px" type="flex" align="bottom">
      <Col span="6">
        <pager-tit title="开卡充值" style="float: left"></pager-tit>
      </Col>
      <Col span="18">
        <Row type="flex" justify="end" :gutter="8">
          <Col span="4">
            <Input size="large" v-model="param.jlXm" clearable placeholder="请输入教练姓名"/>
          </Col>
          <Col span="4">
            <Input size="large" v-model="param.jlJx" clearable placeholder="请输入驾校"/>
          </Col>
          <Col span="1" align="center" style="margin-right: 10px">
            <Button type="primary" @click="getData">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
          </Col>
          <Col span="2" align="center">
            <Tooltip content="添加">
              <Button type="primary" @click="DrawerVal = true">
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

    <!--<Row>-->
    <!--<Table ref="table" size="small" :columns="columns1" :data="carList"></Table>-->
    <!--</Row>-->
    <Modal
      title="添加充值卡"
      v-model="DrawerVal"
      :closable="false"
      width="720"
      :mask-closable="false">

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
<!--                <span style="color: red;font-size: 18px">*初始密码为123456</span>-->
              </FormItem>
            </div>
            <div style="padding-top: 22px;">
              <Button type="primary" @click="compName ='addjl'">
                <Icon type="md-add"/>
              </Button>
            </div>
          </Col>

        </Row>


        <component :is="compName" :jxmc="jlJx"
                   @SaveOk="addjlSaveOk"
                   @colse="clearYY"
                   @remove="getCoachList('',true)"
                   @JLRowClick="JLRowClick"
                   @jxSeljxSel="(val)=>{getCoachList('',true)}"></component>

      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">取消</Button>
        <Button type="primary" @click="save">添加</Button>
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
                <FormItem label="持卡人" label-position="top">
                  <span style="font-size: 15px">{{payItem.jlXm}}</span>
                </FormItem>
                <!--<span style="margin-right: 20px">持卡人</span>-->
                <!--<span style="font-size: 15px">{{payItem.jlXm}}</span>-->
              </Col>
              <Col span="11">
                <FormItem label="卡号" label-position="top">
                  <span style="font-size: 15px">{{payItem.cardNo}}</span>
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
                  <Input v-model="payItem.je"/>
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
      :title="infoItem.jlXm+'信息修改'"
      v-model="info"
      :closable="false"
      width="720"
      :mask-closable="false">

      <Form :model="formData" label-position="top">
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
                  <span style="font-size: 15px">{{infoItem.cardNo}}</span>
                </FormItem>
              </Col>
            </Row>
            <Row style="display: flex;justify-content: space-between">
              <Col span="11">
                <FormItem label="教练驾校" label-position="top">
<!--                  <Input v-model="infoItem.jlJx"/>-->
                  <Select v-model="infoItem.jlJx" style="width:200px">
                    <Option v-for="item in JX" :value="item.val" :key="item.val">{{ item.val }}</Option>
                  </Select>
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
        <Button type="primary" @click="updateInfo">更改</Button>
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

    <mxb :itemObj="jlItem" :isMxb="isMxb" @closemxb="closeMXB"></mxb>
    <component :is="componentName" :printClose="printClose" :hisPrintMess="hisPrintMess"></component>
  </div>
</template>

<script>
  import addjl from '../../lcsf/comp/addJL'
  import mxb from '../jlcz/mxb'
  import {mapMutations} from 'vuex'

  export default {
    name: "index",
    components: {
      addjl,
      mxb
    },
    data() {
      return {
        dataList: [],
        isMxb: false,
        jlItem: {},
        pay: false,        //充值modal
        payItem: {
          je:'',
          sfje:'',
          no:''
        },
        password:false,
        passwordItem:{},
        totalS:0,
          info:false,
          infoItem:{},

        fylist: [],
        v: this,
        DrawerVal: false,
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
          pageSize: 8,
          jlXm: '',
          jlJx: ''
        },
        showCAR: false,
        carMess: null,
        IntervalKE: setInterval(() => {
          this.Ch_LcTime()
        }, 1000),
        columns1: [
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
            key: 'cardJe',
            align: 'center',

          },
          {
            title: '开放日余额',
            key: 'ye',
            align: 'center',

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
                  {props: {placement: 'top', content: '明细',}},
                  [
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0'},
                      on: {
                        click: () => {
                          this.jlItem = p.row
                          this.isMxb = true
                        }
                      }
                    }, '明细')
                  ]
                ),
              );


              buttons.push([
                h('Dropdown',
                  {props: {trigger: "click"}},
                  [
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small',
                      },
                      style: {margin: '0 10px 0 0'},
                      on: {
                        click: () => {
                        }
                      }
                    }, '操作'),
                    h('DropdownMenu',
                      {slot: "list"},
                      [
                        h('DropdownItem', {
                            nativeOn: {
                              click(name) {
                                v.payItem = p.row
                                v.pay = true
                              }
                            }
                          }, '充值'
                        ),h('DropdownItem', {
                              nativeOn: {
                                  click(name) {
                                      v.infoItem = p.row
                                      v.info = true
                                  }
                              }
                          }, '维护'
                      ),
                        // h('DropdownItem', {
                        //   nativeOn: {
                        //     click() {
                        //       v.password=true;
                        //       v.passwordItem=p.row
                        //     }
                        //   }
                        // }, '修改密码'),
                        // h('DropdownItem', {
                        //   nativeOn: {
                        //     click(name) {
                        //       v.resetPw(p.row)
                        //     }
                        //   }
                        // }, '重置密码')
                      ]
                    )
                  ])
              ])
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
          JX:[]
      }
    },
    watch: {
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

      // this.getCarList();
      // this.getzdlist();
      // this.getYYdj();

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
        this.$http.get('/api/lcwxjl/getWxJl', {
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
        this.jlItem = {}
        this.isMxb = false
      },
      closePay(){
        this.payItem={}
        this.pay=false
      },
      savePay(){
        this.$http.post('/api/lcjl/cz', this.payItem).then(res => {
          if (res.code == 200) {
            this.pay=false;
            this.payItem = {};
            this.getData();
            this.swal({
              title: '充值成功',
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
        closeInfo(){
            this.info=false
            this.infoItem={}
        },
        updateInfo(){
            this.$http.post('/api/lcwxjl/update', this.infoItem).then(res => {
                if (res.code == 200) {
                    this.info=false
                    this.infoItem={}
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
      closePw(){
        this.password=false
        this.passwordItem={}
      },
      updatePw(){
        var v=this
        this.swal({
          title: '确认更改'+v.passwordItem.jlXm+'的密码?',
          type: 'warning',
          confirmButtonText: '确认',
          cancelButtonText: '关闭',
          showCancelButton: true
        }).then((res) => {
          if (res.value) {
            this.$http.post('/api/lcwxjl/updatePwd', v.passwordItem).then((res) => {
              if (res.code == 200) {
                v.getData();
                v.passwordItem={}
                v.password=false
                v.swal({
                  title: '修改成功',
                  type: 'success',
                  confirmButtonText: '确认',
                  cancelButtonText: '关闭',
                  showCancelButton: true
                })
              }else {
                v.$Message.error(res.message)
              }
            })
          } else {

          }
        })
      },
      resetPw(item){
        this.swal({
          title: '确认重置'+item.jlXm+'的密码?',
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
              }else {
                this.$Message.error(res.message)
              }
            })
          } else {

          }
        })
      },
      pageChange(val){
        this.param.pageNum = val
        this.getData();
      },
      pageSizeChange(val){
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
        getJX(){
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
