<template>
    <div>
      <Modal v-model="modal2" :closable="false" fullscreen footer-hide>
        <div style="text-align:center">
          <div class="boxbackborder box_col">
            <Row type="flex" justify="end" :gutter="8" style="margin:8px 0;">

              <Col span="5" style="margin-right: -40px">
                <DatePicker v-model="dateRange.jssj"
                            @on-change="param.jssjInRange = v.util.dateRangeChange(dateRange.jssj)"
                            @on-open-change="pageSizeChange(param.pageSize)"
                            format="yyyy-MM-dd"
                            split-panels
                            :options="options2"
                            type="daterange" :placeholder="'请输入时间'"></DatePicker>
              </Col>
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
              <Col span="2" align="center">
                <Button type="primary" @click="plzf">
                  确认支付
                </Button>
              </Col>
              <Col span="2">
                <Button type="error" size="large" long  @click="close">关闭</Button>
              </Col>
            </Row>
            <Table :height="800" stripe
                   size="small"
                   @on-select="tabcheck"
                   :columns="tableColumns" :data="pageData"></Table>
            <!--      <table-area :parent="v"></table-area>-->
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
        </div>
      </Modal>
      <component :is="componentName" :printClose="printClose" :hisPrintMess="hisPrintMess"></component>
    </div>
</template>

<script>
    import print from '../comp/print'
    export default {
        name: "kemxjl",
        components: {
            print,
        },
        data() {
            return {
                componentName: '',
                printClose: false,
                hisPrintMess: '',
                modal2:true,
                apiRoot: this.apis.lcjl,
                choosedItem: null,
                tableColumns: [
                    {
                        type: 'index2', align: 'center', minWidth: 80,
                        render: (h, params) => {
                            return h('span', params.index + (this.param.pageNum - 1) * this.param.pageSize + 1);
                        }
                    },
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {title: '教练姓名', key: 'jlXm', searchKey: 'jlXmLike', minWidth: 90},
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

                    {title: '开始时间', key: 'kssj', minWidth: 140},
                    {title: '结束时间', key: 'jssj', searchType: 'daterange', minWidth: 140},
                    {title: '时长(分钟)', key: 'sc', minWidth: 80},
                    // {title: '学员数量', key: 'xySl', minWidth: 90, defaul: '0'},
                    // {title: '计费类型', key: 'lcLx',minWidth:90,dict:'ZDCLK1048'},
                    {title: '练车费用(元)', key: 'lcFy', append: '元', minWidth: 90, defaul: '0'},
                    {
                        title: '订单状态', key: 'zfzt', minWidth: 80,
                        render: (h, p) => {
                            if (p.row.zfzt == '00') {

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
                    {title: '凭证', key: 'pz', minWidth: 180,},
                    {
                        title: '操作', minWidth: 60, fixed: 'right', render: (h, p) => {
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
                    orderBy: 'jssj desc',
                    jssjIsNotNull: '1',
                    total: 0,
                    lcKm: 2,
                    jssjInRange: '',
                    zhLike: '',
                    pageNum: 1,
                    pageSize: 20,
                },
                options2: {
                    shortcuts: [
                        {
                            text: '一周',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                                return [start, end];
                            }
                        },
                        {
                            text: '一个月',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                                return [start, end];
                            }
                        },
                        {
                            text: '3个月',
                            value () {
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
                ls: {
                    ls1: false,
                    ls2: false,
                    ls3: false,
                },
                QRmodal: false,
                QRmess: {},
                fylist: [],
                v: this,
                DrawerVal: false,
                compName: '',
                componentName: '',
                printClose: false,
                hisPrintMess: '',
                clId: '',
                showFQfzkp: false,
                searchCoachList: [],
                loadingJly: false,
                jlJx: '',
                zxNum: 0,
                xxNum: 0,
                param1: {
                    notShowLoading: 'true',
                    clKm: '2',
                    clBh: '',
                    orderBy: 'clZt asc,clBh asc,clCx asc'
                },
                showCAR: false,
                carMess: null,
            }
        },
        created(){
            var v = this
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            this.dateRange.jssj = [start,end]
            var d = start;
            var c = end;
            var datetimed =this.AF.trimDate(start)+ ' '+'00:00:00';
            var datetimec = this.AF.trimDate() + ' 23:59:59';
            this.param.jssjInRange = datetimed+','+datetimec
            v.param.pageSize = 20;
            console.log(this.param);
            v.util.getPageData(v)
        },
        methods:{
            close(){
                this.$parent.componentName = ''
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
                    r.sc = this.parseTime(r.sc)
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
            tabClick(name) {
                var v = this
                if (name == '0') {
                    this.getCarList()
                } else if (name == '1') {
                    // this.dateRange.jssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59'];
                    // this.param.jssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59';
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                    this.dateRange.jssj = [start,end]
                    this.param.jssjInRange = start+','+end
                    v.param.pageSize = 15;
                    v.util.getPageData(v)
                } else {

                }
            },
        }
    }
</script>

<style scoped>

</style>
