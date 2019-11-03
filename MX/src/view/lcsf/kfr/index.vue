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
    <Row style="margin-bottom: 8px" type="flex" align="bottom">
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


          <Col span="3">
            <DatePicker v-model="dateRange.jssj"
                        @on-change="param.jssjInRange = v.util.dateRangeChange(dateRange.jssj)"
                        @on-open-change="v.util.dateRangeChange(dateRange.jssj)"
                        format="yyyy-MM-dd"
                        split-panels
                        type="daterange" :placeholder="'请输入时间'"></DatePicker>
          </Col>
          <Col span="3">
            <Input size="large" v-model="param.jlXmLike" clearable  placeholder="请输入教练姓名"  @on-enter="v.util.getPageData(v)"/>
          </Col>
          <Col span="2" align="center" >
            <Button type="primary" @click="v.util.getPageData(v)">
              <Icon type="md-search"></Icon>
              <!--查询-->
            </Button>
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
      width="500"
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
                <Select v-model="formData.zddm" style="width:200px">
                  <Option v-for="(it,index) in fylist" :value="it.zddm" :key="index" v-if="it.zddm=='K2KF'">{{it.by9}}</Option>
                </Select>
                <!--              <CheckboxGroup v-model="formData.lcFy">-->
                <!--                <Checkbox label="900"></Checkbox>-->
                <!--              </CheckboxGroup>-->
              </FormItem>
            </div>
          </Col>
        </Row>
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

        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='kf'">
          <Col span="12">
            <FormItem label="学员人数" label-position="top">
              <Input v-model="formData.xySl" type="number"/>
            </FormItem>
          </Col>
        </Row>


        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='py'">
          <Col span="12">
            <FormItem label="学员姓名" label-position="top">
              <Input v-model="formData.xyXm"/>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem label="学员电话" label-position="top">
              <Input v-model="formData.xyDh"/>
            </FormItem>
          </Col>
        </Row>

        <Row :gutter="32" style="padding-top: 5px" v-if="mxlx==='py'">
          <Col span="12">
            <FormItem label="学员身份证号" label-position="top">
              <Input v-model="formData.xyZjhm"/>
            </FormItem>
          </Col>
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
        <FormItem label="备注" label-position="top">
          <Input type="textarea" v-model="formData.bz" :rows="4"/>
        </FormItem>
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

    export default {
        name: "index",
        components: {
            carCard, jlwh, addjl,
            print, radioCar, carStatistics,
            keyypd,
        },
        data() {
            return {
                mxlx: '',
                total: 0,
                giveCar: giveCar,
                v: this,
                apiRoot: this.apis.lcjl,
                choosedItem: null,
                tableColumns: [
                    {
                        title: '序号', align: 'center', minWidth: 80,
                        render: (h, params) => {
                            return h('Tag', {
                                props: {
                                    type: 'volcano',
                                }
                            }, params.index+1)
                        }
                    },
                    {title: '教练姓名', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 90},
                    {title: '教练电话', key: 'jlDh', minWidth: 90},
                    {title: '驾校', key: 'jlJx', minWidth: 90},
                    // {
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
                    {title: '创建时间', key: 'kssj', searchType: 'daterange', minWidth: 140},
                    // {title: '安全员姓名', key: 'zgXm',minWidth:100},
                    // {title: '时长', key: 'sc', minWidth: 80, defaul: '0'},
                    {title: '学员数量(人)', key: 'xySl', minWidth: 90, defaul: '0'},
                    {title: '练车费用(元)', key: 'lcFy', minWidth: 90, defaul: '0'},
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
                            return h('div', buttons);
                        }
                    }
                ],
                DrawerVal: false,
                compName: '',
                componentName: '',
                printClose: false,
                hisPrintMess: '',
                clId: '',
                showFQfzkp: false,
                formData: {
                    zddm:'K2KF',
                    zgXm:'',
                    lcKm: 2,
                    lcLx: '30',
                    lcFy: '',
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
                    id:'',
                    jlXm:'',
                    jlDh:'',
                    sc:''
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
                    orderBy: 'jlId asc,jssj desc',
                    // kssjIsNotNull: '1',
                    total: 0,
                    lcKm: 2,
                    lcLx: '30',
                    cjsjInRange: '',
                    zhLike: ''
                },
                pageData: [],
                specialPageSize: 99999999,
                dateRange: {
                    kssj: ''
                },
                showCAR: false,
                carMess: null,
                IntervalKE: setInterval(() => {
                    // this.Ch_LcTime()
                    this.jump()
                }, 60000),
                fylist: []
            }
        },
        watch: {
            'formData.xySl':function(val, oldVal){//普通的watch监听
                this.formData.lcFy = val*300
            },
            DrawerVal: function (n, o) {
                var v = this
                if (n == false) {
                    this.compName = ''
                    this.formData = {}
                    this.formData.jlCx='C1'
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
            this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
            this.param.cjsjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
            this.util.initTable(this);
            this.getCoachList()
            this.getCarList();
            // setTimeout(() => {
            //   this.jump()
            // }, 1000)
            // this.getYYdj()
            this.getzdlist()
        },
        beforeDestroy() {
            clearInterval(this.IntervalKE)
        },
        methods: {
            getzdlist() {
                this.$http.post('/api/lcjl/Tc',{km:'2',by5:'30'}).then((res)=>{
                    if (res.code == 200){
                        this.fylist = res.result
                        for (let r of this.fylist){
                            r.editMode = false
                            r.zdmc = parseInt(r.zdmc)
                            r.by3 = parseFloat(r.by3)
                            r.by4 = parseFloat(r.by4)
                            if(r.zddm =='k2JS'){
                                r.by9 ='计时' + r.zdmc+'元/小时'
                            } if(r.zddm =='K2PY'){
                                r.by9 = '培优'+ r.zdmc+'元/人'
                            }if(r.zddm =='K2KF1'){
                                r.by9 = '开放日1套餐'+ r.zdmc+'元'
                            }if(r.zddm =='K2KF2'){
                                r.by9 = '开放日2套餐'+ r.zdmc+'元'
                            }if(r.zddm =='K2KF3'){
                                r.by9 = '开放日3套餐'+ r.zdmc+'元'
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
                    if ((r.kssj && r.kssj.length > 0) && (!r.jssj || r.jssj == '')&&(r.lcLx!='20' ||r.lcLx!='30')) {
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
                for (let r of list) {
                    r.sc = this.parseTime(r.sc)
                    r.kssj = r.kssj.substring(0, 16)
                    r.jssj = r.jssj.substring(0, 16)
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
                this.carMess = null;
                this.formData = {};
                this.formData.jlCx='C1'
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
            print(mess) {//还车
                this.hisPrintMess = mess
                // setTimeout(()=>{
                //   this.$refs['backcar'].doPrint()
                // },1000)
                this.componentName = 'print'
            },
            printHc(mess) {
                this.hisPrintMess = mess
                this.componentName = 'print'
                // console.log('dayin')
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
            removeYY(id){
                this.$http.post('/api/lcjl/remove/'+id).then((res) => {
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
                this.$http.post('/api/lcjl/save', this.formData).then(res => {
                    if (res.code == 200) {
                        this.DrawerVal = false;
                        this.util.initTable(this);
                        this.carMess = null
                        console.log(res.message)
                        if (this.mxlx == 'py' || this.mxlx == 'kf') {
                            //打印票据
                            this.formData.id =JSON.parse(res.message).id
                            this.formData.jssj =JSON.parse(res.message).jssj
                            this.formData.jlXm=JSON.parse(res.message).jlXm
                            this.formData.jlJx=JSON.parse(res.message).jlJx
                            this.formData.sc='-'
                            this.formData.yhsc='5分钟'
                            this.formData.kc='科目二'
                            this.formData.clBh='-'
                            this.formData.lcKm='2'

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
                    this.formData =  {
                        zgXm:'',
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
                        id:'',
                        jlXm:'',
                        jlDh:'',
                        sc:''
                    }
                })

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
