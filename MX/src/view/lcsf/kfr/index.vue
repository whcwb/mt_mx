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
      <!--      <Col span="4">-->
      <!--        &lt;!&ndash;<div style="float: left;margin-top: 8px;cursor: pointer">&ndash;&gt;-->
      <!--        &lt;!&ndash;<span style="width: 100px;height: 80px;background-color: #ff9900;color:white;padding:6px;border-radius: 4px;margin-left: 16px;" @click="formData.clZt = '',getCarList()">共{{carList.length}}台</span>&ndash;&gt;-->
      <!--        &lt;!&ndash;<span style="width: 100px;height: 80px;cursor: pointer;background-color: red;color:white;padding:6px;border-radius: 4px;margin-left: 16px;"&ndash;&gt;-->
      <!--        &lt;!&ndash;@click="formData.clZt = '01',getCarList()">&ndash;&gt;-->
      <!--        &lt;!&ndash;在训{{zxNum}}台</span>&ndash;&gt;-->
      <!--        &lt;!&ndash;<span style="width: 100px;height: 80px;background-color: #66CD00;color:white;padding:6px;border-radius: 4px;margin-left: 16px;cursor: pointer;"&ndash;&gt;-->
      <!--        &lt;!&ndash;@click="formData.clZt = '00',getCarList()"&ndash;&gt;-->
      <!--        &lt;!&ndash;&gt;空闲{{xxNum}}台</span>&ndash;&gt;-->
      <!--        &lt;!&ndash;</div>&ndash;&gt;-->
      <!--      </Col>-->
      <!--      <Col span="8">-->
      <!--        <div class="box_row">-->
      <!--          &lt;!&ndash;          <div  style="font-size: 24px;color: #e91b10;line-height: 45px;">&ndash;&gt;-->
      <!--          &lt;!&ndash;            累计：{{total}} 元&ndash;&gt;-->
      <!--          &lt;!&ndash;          </div>&ndash;&gt;-->
      <!--          &lt;!&ndash;          <div @click="compName='keyypd'" style="font-size: 24px;color: #2baee9;line-height: 45px;margin: 0 6px"> 当前排队中</div>&ndash;&gt;-->
      <!--          &lt;!&ndash;          <div style="margin: 0 6px">&ndash;&gt;-->
      <!--          &lt;!&ndash;            <Button style="font-size: 20px;font-weight: 600" @click="componentName='keyypd'" type="error">{{yyrs}}</Button>&ndash;&gt;-->
      <!--          &lt;!&ndash;          </div>&ndash;&gt;-->

      <!--        </div>-->
      <!--      </Col>-->
      <Col span="24">
        <Row type="flex" justify="end" :gutter="8">
          <!--        <Col span="6" style="padding: 10px 20px">-->
          <!--          <Button type="warning" @click="plzf">批量结算</Button>-->
          <!--        </Col>-->


          <!--<Col span="4">-->
            <DatePicker v-model="dateRange.cjsj"
                        @on-change="param.cjsjInRange = v.util.dateRangeChange(dateRange.cjsj)"
                        @on-ok="v.util.getPageData(v)"
                        confirm
                        format="yyyy-MM-dd"
                        style="margin-right: 5px"
                        split-panels
                        type="daterange" :placeholder="'请输入时间'"></DatePicker>
          <!--</Col>-->
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
            <Button type="primary" @click="formData.zddm='K2KF';faCar('kf')">
              <Icon type="md-add"></Icon>
              <!--查询-->
            </Button>
          </Col>
          <!--          <Col span="1" align="center" style="margin-right: 10px">-->

          <!--          </Col>-->
        </Row>
      </Col>
    </Row>
    <div>
      <Row>
        <table-area :pager="false" :parent="v"></table-area>


        <!--          <Row style="padding: 5px 10px">-->
        <!--            <Button class="rbutton" size="large" type="Default" long @click="faCar('kk')">开卡训练</Button>-->
        <!--          </Row>-->
        <!--          <Row style="padding: 5px 10px">-->
        <!--            <Button class="rbutton" size="large" type="Default" long @click="faCar('py')">培优训练</Button>-->
        <!--          </Row>-->
        <!--          <Row style="padding: 5px 10px">-->
        <!--            <Button class="rbutton" size="large" type="Default" long @click="faCar('kf')">开放训练</Button>-->
        <!--          </Row>-->
        <!--          <Row style="padding: 5px 10px">-->
        <!--            <Button class="rbutton" size="large" long type="Default"-->
        <!--                    @click="giveCar.overCar(v,'2'),printClose=true">-->
        <!--              结束训练-->
        <!--            </Button>-->
        <!--          </Row>-->
        <!--<Row style="padding: 5px 10px">-->
        <!--<Button class="rbutton" size="large" type="Default" long @click="yyClick">预约</Button>-->
        <!--</Row>-->
        <!--<Row style="padding: 5px 10px">-->
        <!--<Button class="rbutton" size="large" type="Default" long @click="componentName='keyypd'">预约排队:{{yyrs}}</Button>-->
        <!--</Row>-->

      </Row>

      <Row style="display: flex;align-items: center;height: 36px">
        <Col span="3" align="left">
          <i-switch v-model="switch1"></i-switch>
        </Col>
        <Col span="21" align="right" v-if="switch1">
          <span>
            <span style="font-size: 24px;font-weight: 600">
            人数：<span style="color: #ed3f14"> {{rs}} </span> 人
            </span>
          &nbsp&nbsp&nbsp
            <span style="font-size: 24px;font-weight: 600">
            合计：<span style="color: #ed3f14"> {{hj}} </span> 元
            </span>
            </span>
        </Col>
      </Row>

    </div>
    <!--<div class="box_col_auto" style="background-color: #f2f2f2">-->
    <!--<div class="box_row_list">-->
    <!--<div v-for="(it,index) in carList">-->
    <!--<car-card :carMess="it" @carClick="carClick" @print="print" @his="his(it),printClose=false"></car-card>-->
    <!--</div>-->
    <!--</div>-->
    <!--</div>-->
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
                <Select v-model="formData.zddm" @on-change="lcFyChange" style="width:200px">
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="!it.zddm.includes('K2JS')">
                    {{it.by9}}
                  </Option>
                </Select>
                <!--              <CheckboxGroup v-model="formData.lcFy">-->
                <!--                <Checkbox label="900"></Checkbox>-->
                <!--              </CheckboxGroup>-->
              </FormItem>
            </div>
          </Col>
        </Row>
        <Row :gutter="32" style="padding-top: 5px"  v-if="formData.zddm == 'K2PY'">
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
<!--        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2PY'">-->
<!--          <Col span="8">-->
<!--            <FormItem label="安全员" label-position="top">-->
<!--              <Input v-model="formData.zgXm"/>-->
<!--            </FormItem>-->
<!--          </Col>-->
<!--        </Row>-->

<!--        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2PY'">-->
<!--          <Col span="8">-->
<!--            <FormItem label="学员姓名" label-position="top">-->
<!--              <Input v-model="formData.xyXm"/>-->
<!--            </FormItem>-->
<!--          </Col>-->
<!--          <Col span="8">-->
<!--            <FormItem label="学员电话" label-position="top">-->
<!--              <Input v-model="formData.xyDh"/>-->
<!--            </FormItem>-->
<!--          </Col>-->
<!--          <Col span="8">-->
<!--            <FormItem label="学员身份证号码" label-position="top">-->
<!--              <Input v-model="formData.xyZjhm"/>-->
<!--            </FormItem>-->
<!--          </Col>-->
<!--        </Row>-->
        <!--<radio-car v-if="carMess == null"-->
        <!--clKm="2"-->
        <!--@getCarItemMess="getCarItemMess"-->
        <!--&gt;</radio-car>-->

        <component :is="compName" :jxmc="jlJx"
                   @SaveOk="addjlSaveOk"
                   @colse="clearYY"
                   @remove="getCoachList('',true)"
                   @JLRowClick="JLRowClick"
                   @jxSeljxSel="(val)=>{getCoachList('',true)}"></component>

        <Row :gutter="32" style="padding-top: 5px" v-if="formData.zddm == 'K2KF'">
          <Col span="11">
            <FormItem label="学员人数" label-position="top">
              <Input v-model="formData.xySl" type="number" @on-enter="save"/>
            </FormItem>
          </Col>
        </Row>


        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='py'">
          <Col span="12">
            <FormItem label="金额" label-position="top">
              <CheckboxGroup v-model="formData.lcFy">
                <Checkbox label="900"></Checkbox>
              </CheckboxGroup>
              <!--              <Select v-model="formData.lcFy"-->
              <!--                      filterable-->
              <!--                      clearable-->
              <!--                      remote-->
              <!--              >-->
              <!--                <Option v-for="(it,index) in fylist" :value="it.by5" :key="index">{{it.by5}}</Option>-->
              <!--              </Select>-->
              <!--              <Input v-model="formData.xyZjhm"/>-->
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
<!--        <FormItem label="备注" label-position="top">-->
<!--          <Input type="textarea" v-model="formData.bz" :rows="4"/>-->
<!--        </FormItem>-->
      </Form>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="close">取消</Button>
        <Button type="primary" @click="save">确定</Button>
        <Button type="primary" @click="yy" v-if="mxlx==='kk'">预约</Button>

      </div>
    </Modal>
    <!--    <yyModel ref="yyModel"-->
    <!--             @close="close"-->
    <!--             @getCarList='getCarList'-->
    <!--    ></yyModel>-->
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

  export default {
    name: "index",
    components: {
      carCard, jlwh, addjl,
      print, radioCar, carStatistics,
      keyypd,
    },
    data() {
      return {
          Pmess: {},
          AMess: [
              {cartype:'C1'}
          ],
        hj: 0,
        rs: 0,
        mxlx: '',
        switch1: true,
        total: 0,
        giveCar: giveCar,
        v: this,
        apiRoot: this.apis.lcjl,
        choosedItem: null,
        tableColumns: [
          {
            title: '序号', align: 'center', minWidth: 80,
            // render: (h, params) => {
            //   return h('div', params.index + 1)
            // return h('Tag', {
            //   props: {
            //     type: 'volcano',
            //   }
            // }, params.index + 1)
            // }
            render: (h, p) => {
              return h('div', p.index + 1)
            },
          },
          {title: '驾校', key: 'jlJx', align: 'center',minWidth: 90},
          {title: '教练员', key: 'jlXm', align: 'center',searchKey: 'jlXmLike', minWidth: 90},
          {title: '教练员电话', align: 'center',key: 'jlDh', minWidth: 90},
          {
            title: '类型', minWidth: 90,align: 'center',
            render: (h, p) => {
              return h('div', p.row.lcLx == '20' ? '培优' : '开放日')
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
          {title: '学员数量',align: 'center', key: 'xySl', minWidth: 90, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.xySl+'人')
            },
          },
          // {
          //   title: '安全员', minWidth: 90,
          //   render: (h, p) => {
          //     return h('div', p.row.zgXm == '' ? '/' : p.row.zgXm)
          //   }
          // },
          // // {
          //     title: '车辆类型', key: 'jlCx', minWidth: 90, render: (h, p) => {
          //         return h('Button', {
          //             props: {
          //                 type: 'error',
          //                 size: 'small'
          //             },
          //             style: {
          //                 borderRadius: '15px'
          //             }
          //         }, p.row.jlCx)
          //     }
          // },
          // {title: '安全员姓名', key: 'zgXm',minWidth:100},
          // {title: '时长', key: 'sc', minWidth: 80, defaul: '0'},
          {title: '练车费用',align: 'center', minWidth: 90, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy+'元')
            },},
          {title: '创建时间', align: 'center',key: 'kssj', searchType: 'daterange', minWidth: 140},
          // {
          //     title: '状态', minWidth: 120, render: (h, p) => {
          //         let s = '';
          //         if (!p.row.kssj || p.row.kssj === '') {
          //             s = '预约中'
          //         } else if ((p.row.kssj && p.row.kssj.length > 0) && (!p.row.jssj || p.row.jssj == '')) {
          //             s = '训练中'
          //         } else {
          //             s = '已结束'
          //         }
          //         return h('div', s);
          //     }
          // },
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
              }
              else {
                buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '打印票据', () => {
                  this.hisPrintMess = p.row
                  this.componentName = 'print'
                }));
                // if ((p.row.kssj && p.row.kssj.length > 0) && (!p.row.jssj || p.row.jssj == '')){
                //     buttons.push(this.util.buildButton(this, h, 'error', 'md-card', '结束训练', () => {
                //         if (p.row.lcLx == '20' || p.row.lcLx == '30') {
                //             this.$http.post('/api/lcjl/updateJssj', {id: p.row.id}).then((res) => {
                //                 if (res.code == 200) {
                //                     this.$Message.success(res.message)
                //                     this.util.initTable(this);
                //                 }
                //             })
                //         } else {
                //             this.giveCar.overCar(v, '2'), printClose = true
                //         }
                //
                //     }));
                // }

              }
                if(p.row.lcLx == '20'){
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

                    },p.row.lcLx == '20'?false:true));
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
          xySl: '',
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
          // kssjIsNotNull: '1',
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
        /*IntervalKE: setInterval(() => {
          // this.Ch_LcTime()
          // this.jump()
        }, 60000),*/
        fylist: []
      }
    },
    watch: {
      // 'formData.xySl': function (val, oldVal) {//普通的watch监听
      //   this.formData.lcFy = val * 300
      // },
      DrawerVal: function (n, o) {
        var v = this
        if (n == false) {
          this.compName = ''
          this.formData = {}
          this.formData.jlCx = 'C1'
          this.jlJx = ''
        } else {
          // if (this.formData.lcClId == '') {
          //   this.showCAR = true
          // }
        }
      },
      switch1:function (val) {
        Cookies.set('showMess',val)
      }
    },
    mounted() {
      this.switch1=Cookies.get('showMess')==='true'?true:false
    },
    created() {
      this.dateRange.cjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      this.util.initTable(this);
      this.getCoachList()
      this.getCarList();
      // setTimeout(() => {
      //   this.jump()
      // }, 1000)
      // this.getYYdj()
      this.getzdlist()
        this.pr()
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
        remove(i){
            this.AMess.splice(i,1)
        },
        pushmess() {
            let a = JSON.parse(JSON.stringify(this.Pmess));
            this.AMess.push(a);
        },
        getWXXY(AMess) {
            if (this.formData.zddm.indexOf('K2PY') == -1){
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
                    messarr.push(AMess[i].xyXm+'-'+AMess[i].cartype)
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
        pr(){
            var api = 'https://www.baidu.com/s?wd=%E7%99%BE%E5%BA%A6%E7%9A%84%E7%BD%91%E5%9D%80ip&rsv_spt=1&rsv_iqid=0xd41114de00062d0d&issp=1&f=3&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=0&rsv_dl=ts_0&oq=vue%2520%25E6%258C%2587%25E5%25AE%259Aip%25E5%258F%2591%25E9%2580%2581axios&rsv_t=da21ZuhZm7lWkwXiSRKJoP0FazdoiDT9YLFiJfz636%2BAOuzA4nKH%2FNV87xMowp35sUca&inputT=4160&rsv_pq=9fc868c100415bd8&rsv_sug3=125&rsv_sug1=109&rsv_sug7=100&rsv_sug2=0&prefixsug=%25E7%2599%25BE%25E5%25BA%25A6%25E7%259A%2584&rsp=0&rsv_sug4=6824'
            this.$http.get(api).then((response) => {
                console.log(response.data)
            }).catch(function (error) {
                console.log(error)
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
          // console.log(duration);
          if ((r.kssj && r.kssj.length > 0) && (!r.jssj || r.jssj == '') && (r.lcLx != '20' || r.lcLx != '30')) {
            let min = parseInt(duration / 60000);
            // console.log(min);
            r.sc = duration.subtract(8, 'hour').format("HH时mm分钟");//this.parseTime(min);
            r.lcFy = Math.round(min * 500 / 60);
            r.showlcFy = r.lcFy + '元';
          }
          this.total += parseInt(r.lcFy);
          if (!this.total) {
            this.total = 0;
          }
          // console.log(r.lcFy);
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
          this.AMess=[{cartype:'C1'}];
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
      faCar(name) {
        if (name === 'kk') {
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
      // print(mess) {//还车
      //   this.hisPrintMess = mess
      //   // setTimeout(()=>{
      //   //   this.$refs['backcar'].doPrint()
      //   // },1000)
      //   this.componentName = 'print'
      // },
      // printHc(mess) {
      //   this.hisPrintMess = mess
      //   this.componentName = 'print'
      //   // console.log('dayin')
      // },
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
        // if (this.formData.xySl == '' || this.formData.xySl == 0) {
        //   this.swal({
        //     title: '请填写学员数量',
        //     type: 'error'
        //   })
        //   return
        // }
        // if (this.formData.cardNo == null || this.formData.cardNo == '') {
        // this.readkar();
        // } else {
        //   this.formData.notShowLoading = 'true'
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
          if ( this.getWXXY()){
              this.$http.post('/api/lcjl/save', this.formData).then(res => {
                  if (res.code == 200) {
                      this.DrawerVal = false;
                      this.util.initTable(this);
                      this.carMess = null
                      this.AMess=[{cartype:'C1'}];
                      console.log(res.message, 'resmessage')
                      if (this.mxlx == 'py' || this.mxlx == 'kf') {
                          //打印票据
                          console.log(JSON.parse(res.message));
                          this.formData = JSON.parse(res.message)
                          this.formData.sc = ''
                          this.formData.yhsc = '5分钟'
                          this.formData.kc = '科目二'
                          this.formData.clBh = ''
                          this.formData.lcKm = '2'

                          this.hisPrintMess = this.formData
                          this.componentName = 'print'


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
</style>
