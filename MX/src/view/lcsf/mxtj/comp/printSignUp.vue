<!--
报名收费 打印清单
-->
<style lang="less">
  #textarr{
    textarea{
      border: none;
      outline: none;
    }
  }
  #printDivSigUp {
    font-family: "黑体";
    color: #000000!important;
    font-weight: 500;
    text-align: center;
    padding: 0 0 16px 0;
    .ptintPager {
      width: 220mm;
      margin: auto;
    }
    .titTop {
      text-align: center;
      font-size: 24px;
    }
    .bodyMess {
      overflow: hidden;
    }
    .leftTitSize {
      font-size: 22px;
      padding: 25px 8px 0 8px;
      float: left;
      height: 70mm;
      width: 15mm;
      .LiftTitItem {
        height: 13mm;
        line-height: 13mm;
      }
    }
    .rightTitSize {
      font-size: 12px;
      padding: 35px 8px 8px 0;
      float: right;
      height: 70mm;
      width: 10mm;
      .LiftTitItem {
        height: 5mm;
        line-height: 3mm;
      }
    }
    .pageBox {
      float: left;
      width: 190mm;
      /**/
      .titMess {
        font-size: 16px;
        height: 10mm;
        line-height: 10mm;
      }
      .boxmess {
        border: solid 1px #000;
        padding-right: 8px;
        min-height: 60mm;
        .messList {
          overflow: hidden;
          line-height: 10mm;
          .messTit {
            font-size: 20px;
            width: 35mm;
            text-align: right;
            float: left;
          }
          .ItemMess3 {
            /*border-bottom: solid 1px #000;*/
            text-align: left;
            font-size: 10px;
          }
          .ItemMess {
            float: left;
            clear: right;
            width: 150mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .messTit2 {
            font-size: 18px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess2 {
            float: left;
            width: 25mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 18px;
          }
        }
      }
      .boxmess1 {
        padding-right: 8px;
        .messList {
          overflow: hidden;
          line-height: 10mm;
          .messTit {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess {
            float: left;
            clear: right;
            width: 150mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .ItemMess5 {
            float: left;
            clear: right;
            width: 150mm;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .messTit2 {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .hc{
            word-wrap: normal
          }
          .ItemMess3 {
            float: left;
            width: 20mm;
            /*border-bottom: solid 1px #000;*/
            text-align: left;
            font-size: 10px;
          }
          .ItemMess2 {
            float: left;
            width: 20mm;
            /*border-bottom: solid 1px #000;*/
            text-align: left;
            font-size: 16px;
          }
        }
      }
      .boxPagerNum {
        text-align: left;
        padding: 2px 0;
        font-size: 14px;
      }

    }
  }
</style>
<!-- 报名收费票据打印 -->
<template>
  <div class="signUpPrint">
    <Modal
      v-model="modalShow"
      width="900px"
      :closable='false'
      :mask-closable="false">
      <div slot="header" class="box_row colCenter">
        <div class="box_row_100" style="font-size: 22px;font-weight: 700;margin-left:12px">
          打印
        </div>
        <Button @click="close" style="margin: 0 12px">取消</Button>
        <Button type="success" @click="printClick" style="margin: 0 12px">打印</Button>
      </div>
      <div id="printDivSigUp" class="printBox">
        <div class="ptintPager">
          <div class="titTop">
            知音考场
          </div>
          <div class="bodyMess">

            <div class="leftTitSize">
              <div class="LiftTitItem" v-for="item in ['收','款','凭','证']">
                {{item}}
              </div>
            </div>

            <div class="pageBox">
              <!--tit-->
              <div class="titMess" style="text-align: left;font-weight: 400">
                收款日期 ： {{time}}
                <!--{{printMess[0].chargeRecord.chargeTime}}-->
                <div style="float: right;color: red">
                  NO {{num}}
                </div>
              </div>
              <!--box-->
              <div class="boxmess">

                <div class="messList">
                  <div class="messTit">
                    今收到
                  </div>
                  <div class="ItemMess">
                    :  科目{{hisPrintMess.lcKm==''?'二、三':hisPrintMess.lcKm==='2'?'二':'三'}}
                  </div>
                </div>
                <div class="messList">
                  <div class="messTit">
                    交来
                  </div>
                  <div class="ItemMess">
                    :  模训费
                  </div>
                </div>

                <div class="messList">
                  <div class="messTit">
                    人民币(大写)
                  </div>
                  <div class="ItemMess">
                    :  {{hisPrintMess.addmoney | DX}} <span style="float: right">￥{{hisPrintMess.addmoney}}元</span>
                  </div>
                </div>

<!--                <div class="messList">-->
<!--                  <div class="messTit" >-->
<!--                     练车记录 :-->
<!--                  </div>-->
<!--                  <div class="ItemMess" style="font-size: 10px">-->
<!--                     -->
<!--                  </div>-->
<!--                </div>-->

                <div class="messList">
                  <div class="messTit" style="font-size: 19px;padding-left: 15px">
                    备注 :
                  </div>
                  <div class="ItemMess5" v-for="(item,index) in bzid"  style="text-align: left; font-size: 16px;padding-left: 5px;font-weight: 400">
                    　{{ item }}
                  </div>
                </div>

              </div>
              <div class="boxmess1" style="font-size: 16px;padding-right: 6px;">
                  <div class="messList">
                    <div class="messTit2">
                      核准人
                    </div>
                    <div class="ItemMess2">
                      :
                    </div>
                    <div class="messTit2">
                      制单人
                    </div>
                    <div class="ItemMess2">
                      :
                    </div>
                    <div class="messTit2">
                      收款人
                    </div>
                    <div class="ItemMess2">
                      :
                    </div>
                    <div class="messTit2">
                      交款人
                    </div>
                    <div class="ItemMess2" style="clear:right;">
                      :
                    </div>
                  </div>
<!--                  <div style="overflow: hidden">-->
<!--                    <div style="float: left;width: 33.33%">-->
<!--                      {{payType}}-->
<!--                    </div>-->
<!--                    <div style="float: left;width: 33.33%">-->
<!--&lt;!&ndash;                      推荐人:{{tjr}}&ndash;&gt;-->
<!--                    </div>-->
<!--                    <div style="float: right;width: 33.33%">-->
<!--                      Tel:{{jgphone.length==11 ? '400-133-2133': jgphone}}-->
<!--                    </div>-->
<!--                  </div>-->

              </div>
            </div>
            <div class="rightTitSize">
              <div class="rightTitItem" v-for="item in ['一','联','存','根','二','联','财','务','三','联','客','户']">
                {{item}}
              </div>
            </div>

          </div>
        </div>
      </div>
      <div slot="footer"></div>
    </Modal>
  </div>
</template>

<script>
  import swal from 'sweetalert2'
  import mixin from '@/mixins'
  // import Print from 'print-js'
  import {Printd} from 'printd'
  export default {
    name: "mess",
    mixins: [mixin],
    data() {
      return {
        bzShow:true,
        jgphone: '',//机构电话
        jgName:'',//机构名称
        glyxm:'',//机构管理员
        payType:'',//支付类型
        time: '',
        user: JSON.parse(sessionStorage.getItem('userInfo')),
        num: 0,
        modalShow: true,
        nameList: '',
        money: 0,
        bz: ':',//备注
        tjr:'',
        messIdList:[],
        studentIDS:[],
        bzid:[]
      }
    },
    props: {
        hisPrintMess: Object
    },
    created() {

      this.time=new Date().format('yyyy-MM-dd')
      this.bz = ''
      var v = this
        this.jgphone = 4001332133
        this.jgName = this.hisPrintMess.jx
        this.money = this.hisPrintMess.fdje
        this.bz = this.hisPrintMess.bz
        this.num = (new Date()).valueOf()
    },
    methods: {
      getMess(arr,callback) {
        var v = this

        // console.log(arr);

        // console.log('推荐仁',this.tjr);
        // arr.forEach((item, index) => {
        //
        //   if(this.payType.length == 0){
        //     this.payType = v.dictUtil.getValByCode(v, 'ZDCLK1004', item.chargeRecord.chargeType)
        //   }else {
        //     if(this.payType.indexOf(v.dictUtil.getValByCode(v, 'ZDCLK1004', item.chargeRecord.chargeType))==-1){
        //       this.payType = this.payType + ';' +v.dictUtil.getValByCode(v, 'ZDCLK1004', item.chargeRecord.chargeType)
        //     }
        //   }
        //
        //   this.studentIDS[index] = item.id
        //   this.messIdList[index] = item.chargeRecord.id
        //   this.money += item.realPay
        //   let jgmcArr = ['']
        //
        //   if (index == arr.length - 1) {
        //     this.bz += item.chargeRecord.remark
        //     this.nameList = this.nameList + jgmcArr[0] + item.name + item.carType
        //   } else {
        //     this.bz += item.chargeRecord.remark + "、"
        //     if(this.bz.length>30){
        //       this.bz = this.bz.substring(0,30)
        //     }
        //     this.nameList = this.nameList + jgmcArr[0] + item.name + item.carType + "、"
        //   }
        //
        // })
        callback && callback()
      },
      printClick(){
        var v = this
        // this.BDNum()
        this.bzShow = false
        const cssText = `
        #printDivSigUp {
            /*font-family: "PingFang SC";*/
            font-family: "黑体";
            color: #000000 !important;
            font-weight: 500;
            text-align: center;
            padding: 0 0 16px 0;
          }
          .ptintPager {
            width: 220mm;
            margin: auto;
          }
          .titTop {
            text-align: center;
            font-size: 24px;
          }
          .bodyMess {
            overflow: hidden;
          }
          .leftTitSize {
            font-size: 22px;
            padding: 25px 8px 0 8px;
            float: left;
            height: 70mm;
            width: 10mm;
          }
          .LiftTitItem {
            height: 13mm;
            line-height: 13mm;
          }
          .rightTitItem {
        height: 5mm;
        line-height: 3mm;
      }
          .rightTitSize {
      font-size: 12px;
      padding: 35px 8px 8px 0;
      float: right;
      height: 70mm;
      width: 10mm;

    }
          .pageBox {
            float: left;
            width: 190mm;
          }
          /**/
          .titMess {
            font-size: 16px;
            height: 10mm;
            line-height: 10mm;
          }
         　 .boxmess1 {
        padding-right: 8px;
        .messList {
          overflow: hidden;
          line-height: 10mm;
          .messTit {
            font-size: 16px;
            width: 18mm;
            text-align: right;
            float: left;
          }
          .ItemMess {
            float: left;
            clear: right;
            width: 150mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .messTit2 {
            font-size: 18px;
            width: 18mm;
            text-align: right;
            float: left;
          }
          .ItemMess5 {
            float: left;
            clear: right;
            width: 150mm;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .ItemMess2 {
            float: left;
            width: 30mm;
            text-align: left;
            font-size: 18px;
          }
        }
      }
          .boxmess {
            border: solid 1px #000;
            padding-right: 8px;
            min-height: 60mm;
          }
          .messList {
            overflow: hidden;
            line-height: 10mm;
          }
          .messTit {
            font-size: 20px;
            width: 35mm;
            text-align: right;
            float: left;
          }
          .ItemMess {
            float: left;
            clear: right;
            width: 140mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 2px;
          }
          .messTit2 {
            font-size: 18px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess2 {
            float: left;
            width: 20mm;
            text-align: left;
            font-size: 18px;
          }
          .messTit3 {
            font-size: 16px;
            width: 40mm;
            text-align: right;
            float: left;
          }
          .ItemMess3 {
            float: left;
            width: 50mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 18px;
          }
          .boxPagerNum {
            text-align: left;
            padding: 2px 0;
            font-size: 14px;
          }
        `

        const d = new Printd();
        setTimeout(()=>{
          d.print( document.getElementById('printDivSigUp'), [cssText] )
        },50)
        setTimeout(()=>{
          v.close()
        },300)
      },
      BDNum(){
        var v = this;
        this.$http.post('/api/lcwxjl/savePjbh',{id:this.hisPrintMess.id,pjbh:this.num}).then(res=>{
          if(!res.code == 200){
             return
          }else {
            this.$Message.success(res.message)
          }
        }).catch(err=>{
          return
        })
      },
      close() {
        this.$parent.componentName = ''
      }
    }
  }
</script>
