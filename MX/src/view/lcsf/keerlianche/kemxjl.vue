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
                <Select v-model="param.zfzt" style="width:200px" :placeholder="'请选择支付状态'" @on-change="util.getPageData(v)">
                  <Option value="">全部</Option>
                  <Option value="00">未支付</Option>
                  <Option value="10">已支付</Option>
                </Select>
              </Col>
              <Col span="3">
                <Select v-model="param.jlId"
                        filterable
                        clearable
                        remote
                        loading
                        :placeholder="'请搜索教练'"
                        loading-text="请输入关键字搜索"
                        @on-query-change="searchJly"
                        ref="jlySelect"
                        @on-change="util.getPageData(v)"
                >
                  <Option v-for="(it,index) in searchCoachList" :value="it.value" :key="index">{{it.label}}</Option>
                </Select>
<!--                <Input size="large" v-model="param.jlXmLike" clearable placeholder="请输入教练姓名"-->
<!--                       @on-enter="pageSizeChange(param.pageSize)"/>-->
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
                    {title: '开始时间', key: 'kssj', minWidth: 140},
                    {title: '结束时间', key: 'jssj', searchType: 'daterange', minWidth: 140},
                    {title: '时长(分钟)', key: 'sc', minWidth: 80},
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
                                console.log(p.row);
                                this.componentName = 'print'
                            }));
                            return h('div', buttons);
                        }
                    },

                ],
                pageData: [],
                param: {
                    orderBy: 'jssj desc',
                    jssjIsNotNull: '1',
                    total: 0,
                    zfzt:'',
                    lcKm: 2,
                    jlId:'',
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
                coachList: [],
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
            v.util.getPageData(v);
            this.getCoachList()
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
            getCoachList(id, clear) {
                if (clear) {
                    this.param.jlId = '';
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
                        this.param.jlId = id
                    }
                })
            },
            searchJly(query) {
                if (query !== '') {
                    console.log(query);
                    this.loadingJly = true;
                    setTimeout(() => {
                        this.loadingJly = false;
                        this.searchCoachList = this.coachList.filter(item => {
                            return item.label.indexOf(query.toUpperCase()) != -1
                        });
                    }, 200);
                    console.log(this.searchCoachList);
                    console.log(this.coachList);
                } else {
                    this.searchCoachList = [];
                }
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
