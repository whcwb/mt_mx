<template>
  <div class="box_col">
    <!--<pager-tit title="开放日训练" style="float: left"></pager-tit>-->
    <Menu mode="horizontal" active-name="1" style="margin-bottom: 8px">
      <MenuItem name="1">
        <div style="font-weight: 700;font-size: 16px">
          科目三培优
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
        <Row type="flex" style="justify-content: space-between;align-items: center" :gutter="8">
          <Col span="2" align="center">
            <span
              style="cursor: pointer;width: 60px;height: 80px;border:1px solid #30bff5;color:black;padding:6px;border-radius: 4px;margin-left: 16px;"
              @click="mxShow">培优明细</span>
          </Col>

          <Col span="19" style="display: flex;justify-content: flex-end">
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
            <Col span="3">
              <Select v-model="param.jlCxIn" @on-change="v.util.getPageData(v)" clearable placeholder="请选择车型">
                <Option v-for="(item, index) in carTypes" :value="item.val" :key="index">
                  {{item.label}}
                </Option>
              </Select>
            </Col>
            <Col span="1" align="center" style="margin-right: 5px">
              <Button type="primary" @click="v.util.getPageData(v)">
                <Icon type="md-search"></Icon>
                <!--查询-->
              </Button>
            </Col>
            <Col span="1" align="center">
              <Button type="primary" @click="ifFinish=true,faCar('kf')">
                <Icon type="md-add"></Icon>
              </Button>
            </Col>
          </Col>

        </Row>
      </Col>
    </Row>
    <div>
      <Row>
        <table-area :pager="false" :TabHeight="AF.getPageHeight()-230" :parent="v"></table-area>


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
            <h2>培优训练</h2>
          </div>
        </div>
      </div>
      <Form :model="formData" label-position="top">

        <Row :gutter="32">
          <Col span="12">
            <Row :gutter="20">
            <div style="float: left">
              <FormItem label="教练员" label-position="top" style="width: 250px">
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
            </Row>
            <Row :gutter="28">
              <Col>
                <div style="float: left">
                  <FormItem label="选择车型" label-position="top">
                    <!--<Select v-model="formData.jlCx" @on-change="cxChange" style="width:70px">-->
                    <!--<Option v-for="(it,index) in cxlist" :value="it.key" :key="it.key">-->
                    <!--{{it.key}}-->
                    <!--</Option>-->
                    <!--</Select>-->
                    <Select v-model="cx" @on-change="cxChange" style="width:70px">
                      <Option v-for="(it,index) in ['C','A','B']" :value="it" :key="it">
                        {{it}}
                      </Option>
                    </Select>
                  </FormItem>
                </div>
              </Col>
              <Col span="12">
                <div style="float: left">
                  <FormItem label="计费套餐" label-position="top">
                    <Select v-model="formData.zddm" @on-change="lcFyChange" style="width:165px">
                      <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index"
                              v-if="it.zddm.includes('K3PY')&&it.by8.includes(cx)">
                        {{it.by9}}-{{it.zdmc}}元
                      </Option>
                    </Select>
                  </FormItem>
                </div>
              </Col>

            </Row>
          </Col>
          <Col span="12">
            <Col span="12">
              <div style="float: left">
                <FormItem label="备注" style="width: 280px">
                  <Input type="textarea" v-model="formData.bz" :rows="4"/>
                </FormItem>
              </div>
            </Col>
          </Col>
        </Row>

        <Row :gutter="32" style="padding-top: 5px">
          <Card>
            <p slot="title">学员信息</p>
            <p>
              <Row v-for="(item,index) in AMess" :key="index" style="display: flex;align-items: center;">
                <Col>
                  <RadioGroup v-model="item.cartype">
                    <Radio  v-for="item in cxlist"  :label="item.key" v-if="item.key.includes(cx)"></Radio>
                  </RadioGroup>
                </Col>
                <Col span="4" :class-name="'colsty'">
                  <Input type="text" size="default" :ref="'input'+(index*3+1)" id="input1"  @on-focus="getInputFocus(index*3 +1)" v-model="item.xyXm" placeholder="学员姓名"/>
                </Col>
                <Col span="6" :class-name="'colsty'">
                  <Input type="textarea" :autosize="{minRows: 1,maxRows: 1}" :ref="'input'+(index*3+2)" id="input2" @on-focus="getInputFocus(index*3 + 2)"
                         size="default" v-model="item.bz" placeholder="身份证号码"/>
                </Col>
                <Col span="4" :class-name="'colsty'">
                  <Input type="textarea" :autosize="{minRows: 1,maxRows: 1}" :ref="'input'+(index*3+3)" v-focus="true" @on-focus="getInputFocus(index*3+3)" size="default" v-model="item.xyDh" placeholder="学员联系电话"/>
                </Col>
                <Col span="2" v-if="AMess.length>1">
                  <Button size="default" type="warning" tabIndex="-1" @click="remove(index)">删除</Button>
                </Col>
                <Col span="2" align="center">

                  <Button type="info" icon="md-add" tabIndex="-1"
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
          <Select v-model="aqyItem.zgId" filterable ref="se" style="width: 280px">
            <Option v-for="(item) in sfaemanlist" :value="item.value" :key="item.value">{{item.label}}</Option>
          </Select>
        </Col>
      </Row>
      <div slot='footer'>
        <Button style="margin-right: 8px" @click="updateAQY=false,aqyItem.zgXm=aqyItem.zgId=aqyItem.id=''">取消</Button>
        <Button type="primary" @click="update">确定</Button>
      </div>
    </Modal>


    <Modal
      v-model="mx"
      :closable="false"
      width="1000"
      :mask-closable="false">
      <div slot="header">
        <div class="box_row">
          <div style="font-size: 16px;margin-right: 28px;width: 100%;margin-top: 7px;display: flex;justify-content: space-between">
            <h2>培优明细</h2>
            <div>
              <Button type="primary" @click="download">下载</Button>
              <Button style="margin-left: 8px" @click="mx=false">关闭</Button>
            </div>
          </div>
        </div>
      </div>
      <Row :gutter="32" style="padding-top: 5px">
        <Col span="24">
          <Table size="small" :columns="mxColumns" :data="mxList"></Table>
        </Col>
      </Row>
      <div slot='footer'>

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
    mixins:[mixin],
    components: {
      carCard, jlwh, addjl,
      print, radioCar, carStatistics, printNew,
      keyypd,
    },
    data() {
      return {
        Pmess: {
          cartype: ''
        },
        AMess: [
          {cartype: ''}
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
        carTypes: [{label: "全部", val: "A1,A2,A3,B1,B2,A,B,C1,C2,C"}, {
          label: '大车',
          val: 'A1,A2,A3,B1,B2,A,B'
        }, {label: '小车', val: 'C1,C2,C'}],
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
          {title: '驾校', key: 'jlJx', align: 'center', minWidth: 90},
          {title: '教练员', key: 'jlXm', align: 'center', searchKey: 'jlXmLike', minWidth: 90},
          {title: '教练员电话', align: 'center', key: 'jlDh', minWidth: 90},
          {
            title: '类型', minWidth: 110, align: 'center',
            render: (h, p) => {
              return h('div', '培优' + p.row.zdxm.zdmc + '元')
            }
          },
          {
            title: '学员数量', align: 'center', key: 'xySl', minWidth: 70, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.xySl + '人')
            },
          },
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
          // {title: '时长', key: 'sc', minWidth: 80, defaul: '0'},
          {
            title: '练车费用', align: 'center', minWidth: 90, defaul: '0',
            render: (h, p) => {
              return h('div', p.row.lcFy + '元')
            },
          },
          {title: '创建时间', align: 'center', key: 'kssj', searchType: 'daterange', minWidth: 140},
          {title: '安全员', align: 'center', key: 'zgXm', minWidth: 80},
          {title: '备注', align: 'center', key: 'bz', minWidth: 100},
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
                  console.log(this.hisPrintMess)
                  this.printClose = false
                  this.componentName = 'printNew'
                }));

              }
              if (p.row.lcLx == '20') {
                buttons.push(this.util.buildButton(this, h, 'info', 'ios-construct', '更改安全员', () => {
                  if (p.row.zgXm == '') {
                    this.aqyItem.zgXm = this.aqyItem.zgId = ''
                    this.updateAQYtitle = '添加'
                  } else {
                    this.updateAQYtitle = '更改'
                    this.aqyItem.zgXm = p.row.zgXm
                  }
                  this.aqyItem.zgId = p.row.zgId
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
          id: '',
          zgXm: '',
          zgId: ''
        },
        sfaemanlist: [],
        DrawerVal: false,
        mx: false,
        mxList: [],
        mxColumns: [
          {title: '序号', align: 'center',type:'index', minWidth: 60},
          {title: '教练员', key: 'jlXm', align: 'center', minWidth: 90},
          {
            title: '编号', align: 'center', minWidth: 60,
            render: (h, p) => {
              return h('div', p.row.index + 1)
            },
          },
          {
            title: '学员姓名', minWidth: 110, align: 'center', key: 'xyXm'
          },
          {
            title: '学员证件号码', minWidth: 140, align: 'center', key: 'xyZjhm'
          },
          {
            title: '学员联系方式', minWidth: 110, align: 'center', key: 'xyDh'
          },
          {
            title: '培训车型', minWidth: 110, align: 'center', key: 'xyCx',
            filters: [
              {
                label: '小车',
                value: 1
              },
              {
                label: '大车',
                value: 2
              }, {
                label: 'C1',
                value: 3
              }, {
                label: 'C2',
                value: 4
              }, {
                label: 'A1',
                value: 5
              }, {
                label: 'A2',
                value: 6
              },{
                label: 'A3',
                value: 7
              },{
                label: 'B1',
                value: 8
              },{
                label: 'B2',
                value: 9
              },{
                label: 'B3',
                value: 10
              },
            ],
            filterMultiple: false,
            filterMethod (value, row) {
              if (value === 1) {
                return row.xyCx == 'C1'||row.xyCx == 'C2';
              } else if (value === 2) {
                return row.xyCx.indexOf('A') >-1||row.xyCx.indexOf('B') >-1
              } else if(value === 3){
                return row.xyCx == 'C1'
              } else if(value === 4){
                return row.xyCx == 'C2'
              }else if(value === 5){
                return row.xyCx == 'A1'
              }else if(value === 6){
                return row.xyCx == 'A2'
              }else if(value === 7){
                return row.xyCx == 'A3'
              }else if(value === 8){
                return row.xyCx == 'B1'
              }else if(value === 9){
                return row.xyCx == 'B2'
              }else if(value === 10){
                return row.xyCx == 'B3'
              }
            }
          },
        ],
        compName: '',
        componentName: '',
        printClose: false,
        hisPrintMess: '',
        clId: '',
        showFQfzkp: false,
        cx: 'C',
        formData: {
          zddm: 'K3PY',
          zgXm: '',
          lcKm: 3,
          lcLx: '20',
          cardNo: '',
          clBh: '',
          lcClId: '',
          jlJx: '',
          jlId: "",
          jlCx: '',
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
          lcKm: 3,
          lcLxIn: '20,30',
          cjsjInRange: '',
          zhLike: '',
          jlCxIn: "A1,A2,A3,B1,B2,A,B,C1,C2,C"
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
        fylist: [],
        fy: [],
        cxlist: [],
        focusList: [false, false,false]
      }
    },
    directives: {
      focus: function (el,is) {
        // console.log('sssss',is)
        if(is.value) el.focus();
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
          this.formData.xySl = ''
          // this.formData.jlCx = 'C1'
          this.formData.jlJx = ''
        } else {
          this.cx = 'C'
          // if (this.formData.lcClId == '') {
          //   this.showCAR = true
          // }
        }
      },
      switch1: function (val) {
        Cookies.set('showMess', val)
      }
    },
    mounted() {
      this.switch1 = Cookies.get('showMess') === 'true' ? true : false
      if (Cookies.get('showModal') === 'true') {
        this.DrawerVal = true
        Cookies.set('showModal', 'false')
      }
    },
    created() {
      this.dateRange.cjsj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      this.util.initTable(this);
      this.getCoachList()
      this.getCarList();
      this.getSafemanList()
      this.cxlist = this.dictUtil.getByCode(this, 'ZDCLK0040');
      // setTimeout(() => {
      //   this.jump()
      // }, 1000)
      // this.getYYdj()
      this.getzdlist()
      // this.pr()
      this.zy()
    },
    beforeDestroy() {
      clearInterval(this.IntervalKE)
    },
    methods: {
      zy(){
        var _this = this;
        // console.log(_this.focusList);
        document.onkeydown = function (e) {
          let key = window.event.keyCode;
          if (key == 39) {
            console.log(_this.AMess, "amess")
            // console.log("点击了右键");
            // console.log(_this.$refs['input3']);
            // // _this.$nextTick(() => {
            //   _this.$refs['input3'][0].focus()
            // // })

            // console.log(_this.focusList.length);
            for (let a = 0; a < 9; a++) {  //从左往右，所以下一个input框是a+1
              // console.log(a, "A")
              // console.log(_this.focusList[a]);
              // console.log(_this.$refs['input' + (a + 1)]);
              if (_this.focusList[a] && _this.$refs['input' + (a + 1)]) {
                // console.log('input' + (a + 1));
                _this.focusList[a] = false
                _this.focusList[a + 1] = true;
                _this.$nextTick(()=>{
                  _this.$refs['input' + (a + 1)][0].focus()
                })
                // console.log(_this.focusList,"end");
                return;
              }
            }
          }
          if(key == 37){
            // console.log("点击了左键");
            for (let a = 0; a < _this.focusList.length; a++) {   //从右向左，所以上一个input框是a-1
              if (_this.focusList[a] && _this.$refs['input' + (a - 1)]) {
                _this.focusList[a] = false
                _this.focusList[a - 1] = true;
                _this.$nextTick(()=>{
                  _this.$refs['input' + (a - 1)][0].focus()
                })
                return;
              }
            }
          }
          // if (key == 107){
          //   console.log('++++++++')
          //   _this.pushmess()
          // }
          // if (key == 109){
          //   console.log('--------')
          //   _this.remove(_this.AMess.length-1)
          // }
        };
      },
      getInputFocus(index) {
        // console.log(this.focusList.length , "--------")
        // console.log(index, "index")
        for (let a = 0; a < this.focusList.length; a++) {
          if (index == a) {
            this.focusList[a] = true
          } else {
            this.focusList[a] = false
          }
        }
      },
      remove(i) {

          this.AMess.splice(i, 1)

      },
      pushmess() {
        // if (this.cx == 'C'){
        //   this.Pmess.cartype = 'C1'
        // }
        // if (this.cx == 'B'){
        //   this.Pmess.cartype = 'B2'
        // }
        // if (this.cx == 'A'){
        //   this.Pmess.cartype = 'A1'
        // }
        let a = JSON.parse(JSON.stringify(this.Pmess));
        this.AMess.push(a);
        // let b = [false,false,false]
        this.focusList.push(false,false,false)
      },
      getWXXY(AMess) {
        AMess = this.AMess
        let arrAMess = AMess.length - 1;
        let messarr = [];
        let dxarr = [];
        let sfzarr = [];
        let a = true
        // console.log(AMess, 'AMess');
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
              // console.log(dxarr.join(','))
              // console.log(messarr.join(','))
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
          console.log(response.data)
        }).catch(function (error) {
          console.log(error)
        })
      },
      getzdlist() {
        this.$http.post('/api/lcjl/Tc', {km: '3'}).then((res) => {
          if (res.code == 200) {
            this.fylist = res.result
            for (let r of this.fylist) {
              r.editMode = false
              r.zdmc = parseInt(r.zdmc)
              r.by3 = parseFloat(r.by3)
              r.by4 = parseFloat(r.by4)
            }
            // this.formData.jlCx = 'C1'
            this.cxChange()
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
        // this.formData.jlCx = 'C1'
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
      cxChange(a) {
          // for (let i=0;i<this.AMess.length;i++){
          //
          //   if (this.cx == 'C'){
          //     console.log('c')
          //     this.AMess[i].cartype = 'C1'
          //     console.log( this.AMess)
          //     this.$nextTick()
          //   }
          //   if (this.cx == 'B'){
          //     console.log('B')
          //
          //
          //     this.AMess[i].cartype = 'B2'
          //     console.log( this.AMess)
          //     this.$nextTick()
          //   }
          //   if (this.cx == 'A'){
          //     console.log('A')
          //     this.AMess[i].cartype = 'A1'
          //     console.log( this.AMess)
          //     this.$nextTick()
          //   }
          // }
        // this.fy = []
        // this.fylist.map((val, index, arr) => {
        //   if (val.by8.includes(this.formData.jlCx)) {
        //     this.fy.push(val)
        //   }
        // })
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
      getSafemanList() {
        this.$http.post('/api/zgjbxx/getAqy', {notShowLoading: 'true'}).then((res) => {
          if (res.code == 200) {
            res.result.forEach((item, index) => {
              let py = this.util.parsePY(item.xm)
              item.label = item.xm + ' [' + py + ']'
              item.value = item.id
              if (index == res.result.length - 1) {
                this.sfaemanlist = res.result
              }
            })
            this.aqyItem.zgId = ''
          } else {
            this.$Message.info(res.message);
          }
        })
      },
      update() {
        if (this.aqyItem.zgId == '') {
          this.$Message.info('请选择安全员');
          return
        }

        for (let r of this.sfaemanlist) {
          if (r.id === this.aqyItem.zgId) {
            this.aqyItem.zgXm = r.xm
          }
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
      searchJlyaq(query) {
        // console.log(query);
      },
      mxShow() {
        this.mx = true
        this.mxList = []
        this.pageData.map((val, index, arr) => {
          let valArrLength = val.xyXm.split(',').length
          let xmArr = val.xyXm.split(',')
          let zjhmArr = val.xyZjhm.split(',')
          let dhArr = val.xyDh.split(',')
          for (let i = 0; i < valArrLength; i++) {

            let obj = {index: i}
            obj.jlXm = i == 0 ? val.jlJx+' '+val.jlXm : ''
            obj.xyXm = xmArr[i].split('-')[0]
            obj.xyCx = xmArr[i].split('-')[1]
            obj.xyZjhm = zjhmArr[i]
            obj.xyDh = dhArr[i]

            this.mxList.push(obj)
          }


        })

        // console.log(this.mxList)
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
      download(){
        window.open(this.apis.url + '/pub/exportXymx?'+`notShowLoading=${this.param.notShowLoading}&total=${this.param.total}&lcKm=${this.param.lcKm}&lcLxIn=${this.param.lcLxIn}&cjsjInRange=${this.param.cjsjInRange}&zhLike=${this.param.zhLike}&pageSize=${this.param.pageSize}&clBh=${this.param.clBh}`);
      },
      save() {//发车

        if (this.mxlx == 'py') {
          this.formData.lcLx = '20'


        }
        this.formData.jlCx = this.cx
        this.formData.lcKm = 3
        this.formData.lcLx = '20'

        delete this.formData.lcFy
        if (this.getWXXY()) {
          this.$http.post('/api/lcjl/save', this.formData).then(res => {
            if (res.code == 200) {
              this.DrawerVal = false;
              this.util.initTable(this);
              this.carMess = null
              this.AMess = [{cartype: 'C1'}];
              // console.log(res.message, 'resmessage')
              if (this.mxlx == 'py' || this.mxlx == 'kf') {
                //打印票据
                this.formData = JSON.parse(res.message)
                this.formData.sc = ''
                this.formData.yhsc = '5分钟'
                this.formData.kc = '科目二'
                this.formData.clBh = ''
                this.formData.lcKm = '2'

                this.hisPrintMess = this.formData
                this.printClose = this.ifFinish ? true : false
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
              jlCx: '',
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
