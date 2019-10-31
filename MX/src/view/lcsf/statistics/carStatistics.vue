<template>
  <div>
    <Modal
      v-model="showModal"
      width="1600"
      height="700px"
      :closable='false'
      :mask-closable="false"
      class-name="vertical-center-modal"
      :title="'练车统计'">
      <div style="overflow: auto;height: 100%;width:100%">
        <search-bar :parent="v" :show-create-button="false"></search-bar>
        <Table :height="650" stripe
               @on-select="tabcheck"
               :columns="tableColumns" :data="pageData"></Table>
<!--        <table-area :parent="v" :tab-height="650" :pager="false"></table-area>-->
        <br>
        <Row>
          <Col span="6">
            <span class="total">勾选合计总费用：{{gxfy}}元</span>
            <span><Button type="warning" @click="plzf">批量支付</Button></span>
          </Col>
        </Row>
        <!--<component :is="componentName"></component>-->
      </div>
      <div slot='footer'>
        <Button type="default" @click="close" style="color: #949494">关闭</Button>
      </div>
    </Modal>
    <component :is="componentName" :hisPrintMess="hisPrintMess"></component>
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
          <Card>
            <p slot="title" style="font-size: 20px;font-weight: 600">训练信息</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px">教练 : {{QRmess.jlXm}}</p>
<!--            <p style="font-size: 18px;font-weight: 500;padding: 10px">时长 : {{QRmess.sc}}分钟({{QRmess.kssj}}-{{QRmess.jssj}})</p>-->
            <p style="font-size: 18px;font-weight: 500;padding: 10px">费用 : {{QRmess.lcFy}}元</p>
          </Card>
        </Row>
        <Row>
          <Card>
            <p slot="title" style="font-size: 20px;font-weight: 600">支付方式</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"> <Checkbox disabled v-model="ls.ls3">{{ls.ls6}}</Checkbox>现金支付;</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"><Checkbox disabled v-model="ls.ls2">{{ls.ls6}}</Checkbox>充卡支付(余额:{{QRmess.cardje}}元);</p>
            <p style="font-size: 18px;font-weight: 500;padding: 10px"><Checkbox disabled v-model="ls.ls1">{{ls.ls6}}</Checkbox>抵扣支付(余额:{{QRmess.kfje}}元)</p>
          </Card>
        </Row>
        <Row style="text-align: right">
          <p style="font-size: 20px;font-weight: 600;padding: 10px">{{QRmess.bz}} 元</p>
        </Row>
      </div>
    </Modal>
  </div>
</template>

<script>
  import print from '../comp/print'

  export default {
    name: 'char',
    components: {print},
    data() {
      return {
          ls:{
              ls1:false,
              ls2:false,
              ls3:false,
          },
          QRmodal:false,
          QRmess:{},
        compHisName: '',
        hisPrintMess: {},
        v: this,
        showModal: true,
        pagerUrl: this.apis.lcjl.QUERY,
        dateRange: {kssj: {}},
        choosedItem: null,
        componentName: '',
        tableColumns: [

            {title: '序号', type: 'index',fixed:'left'},
            {
                type: 'selection',
                width: 60,
                align: 'center'
            },
            {title: '教练姓名', key: 'jlXm', searchKey: 'jlXmLike',minWidth:30},
          {title: '开始时间', key: 'kssj', searchType: 'daterange',minWidth:100},
          {title: '结束时间', key: 'jssj',minWidth:100},
          {title: '时长', key: 'sc', append: '分钟',minWidth:30,defaul:'0'},
          {title: '计费类型', key: 'lcLx',minWidth:90,dict:'ZDCLK1048'},
          {title: '费用', key: 'lcFy', append: '元',minWidth:30,defaul:'0'},
          {title: '驾校/队号', key: 'jlJx', searchKey: 'jlJxLike',minWidth:80},

          {title: '安全员姓名', key: 'zgXm', searchKey: 'zgXmLike',minWidth:30},
          {title: '练车科目', key: 'lcKm', dict: 'km',minWidth:40},
          {title: '学员数量', key: 'xySl',minWidth:30,defaul:'0'},
          {title: '订单状态', key: 'zfzt',minWidth:30,
              render:(h,p)=>{
                if (p.row.zfzt == '00'){
                    return h('div','未支付')
                }else {
                    return h('div','已支付')
                }
              }
          },
          {
            title: '操作',fixed:'right',minWidth:70, render: (h, p) => {
              let buttons = [];
              buttons.push(this.util.buildButton(this, h, 'success', 'ios-print', '补打', () => {
                this.hisPrintMess = p.row
                this.componentName = 'print'
              }));
              if(p.row.zfzt == '00'){
                  buttons.push(this.util.buildButton(this, h, 'error', 'logo-yen', '结算', () => {
                     this.$http.post('/api/lcjl/getBatchPay',{ids:p.row.id}).then((res)=>{
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
                         }
                     })
                  }));
              }

              return h('div', buttons);
            }
          },
        ],
        pageData: [],
        param: {
          total: 0,
          zhLike: '',
          pageNum: 1,
          pageSize: 100
        },
        sc: 0,
        xy: 0,
        fy: 0,
        gxfy: 0,
          qrids:''
      }
    },
    created() {
      this.dateRange.kssj = [this.AF.trimDate() + ' 00:00:00', this.AF.trimDate() + ' 23:59:59']
      this.param.kssjInRange = this.AF.trimDate() + ' 00:00:00' + ',' + this.AF.trimDate() + ' 23:59:59'
      this.param.lcClId = this.$parent.clId
      this.tableHeight = window.innerHeight - 600
      this.util.initPageSize(this);
      this.util.fillTableColumns(this)
      this.util.getPageData(this)
    },
    methods: {
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
        QRcancel(){},
        QRok(){
            let ids = ''
            if (this.qrids!=''){
               ids = this.qrids
            }else {
                ids = this.QRmess.id
            }
            this.$http.post('/api/lcjl/batchPay',{ids:ids}).then((res)=>{
                if (res.code == 200){
                    this.$Message.success(res.message)
                    this.util.getPageData(this)
                }else {
                    this.$Message.error(res.message)
                }
            })
        },
      print(item) {
        console.log('print');
        console.log(item);
      },
      afterPager(list) {
        this.sc = 0;
        this.xy = 0;
        this.fy = 0;
        for (let r of list) {
          if (r.xySl) this.xy += r.xySl
          if (r.sc) this.sc += r.sc
          if (r.lcFy) this.fy += r.lcFy
        }
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
      close() {
        this.showModal = false;
        let v = this;
        setTimeout((t) => {
          v.$parent.componentName = "";
        }, 200)
      }
    }
  }
</script>
<style>
  .total {
    color: red;
    font-size: 18px;
    font-weight: 600;
  }
</style>
