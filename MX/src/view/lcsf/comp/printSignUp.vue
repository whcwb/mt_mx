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
      .rightTitItem {
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
          .ItemMess {
            float: left;
            clear: right;
            width: 130mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .messTit2 {
            font-size: 18px;
            width: 20mm;
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
          .messTit3 {
            font-size: 16px;
            width: 30mm;
            text-align: right;
            float: left;
          }
          .ItemMess3 {
            float: left;
            width: 60mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 18px;
          }
        }
      }
      .boxmess1 {
        padding-right: 8px;
        .messList11 {
          overflow: hidden;
          line-height: 10mm;
          .messTit11 {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess11 {
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
          .messTit211 {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess211 {
            float: left;
            width: 20mm;
            /*border-bottom: solid 1px #000;*/
            text-align: left;
            font-size: 18px;
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
              <div class="titMess" style="text-align: left">
                收款日期：{{time}}
                <!--{{printMess[0].chargeRecord.chargeTime}}-->
                <div v-if="num.length == 13" style="float: right">
                  NO {{num}}
                </div>
                <div v-else style="float: right;color: red">
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
                    :  {{jgName}}_{{glyxm}}
                  </div>
                </div>
                <div class="messList">
                  <div class="messTit">
                    交來
                  </div>
                  <div class="ItemMess">
                    :  充值费
                  </div>
                </div>

                <div class="messList">
                  <div class="messTit3">
                   充值金额
                  </div>
                  <div class="ItemMess3">
                    :  {{money | DX}} <span style="float: right">￥{{money | GS}}元</span>
                  </div>
                  <div class="messTit3">
                    赠送额度
                  </div>
                  <div class="ItemMess3">
                    :  {{money1 | DX}} <span style="float: right">￥{{money1 | GS}}元</span>
                  </div>
                </div>
<!--                <div class="messList">-->
<!--                  <div class="messTit">-->
<!--                    &nbsp;-->
<!--                  </div>-->
<!--                </div>-->

                <div class="messList">
                  <div class="messTit" style="font-size: 19px;padding-left: 15px">
                    备注：
                  </div>
                </div>

              </div>
              <div class="boxmess1" style="font-size: 16px;padding-right: 6px;">
                <div class="messList11">
                  <div class="messTit211">
                    核准人
                  </div>
                  <div class="ItemMess211">
                    :
                  </div>
                  <div class="messTit211">
                    制单人
                  </div>
                  <div class="ItemMess211">
                    :
                  </div>
                  <div class="messTit211">
                    收款人
                  </div>
                  <div class="ItemMess211">
                    :
                  </div>
                  <div class="messTit211">
                    付款人
                  </div>
                  <div class="ItemMess211" style="clear:right;">
                    :
                  </div>
                </div>

              </div>
            </div>
            <div class="rightTitSize">
              <div class="rightTitItem" v-for="item in ['一','联','存','根','二','联','客','户','三','联','财','务']">
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
        money1: 0,
        bz: ':',//备注
        tjr:'',
        messIdList:[],
        studentIDS:[]
      }
    },
    props: {
        hisPrintMess: Object
    },
    created() {
      this.getTime()
      this.bz = ''
      var v = this
        this.jgphone = 4001332133
        this.jgName = this.hisPrintMess.jx
        this.glyxm = this.hisPrintMess.xm
        this.money = this.hisPrintMess.sfje
        this.money1 =  parseInt(this.hisPrintMess.je) - parseInt(this.hisPrintMess.sfje)
        this.bz = this.hisPrintMess.bz
        this.getNum()
      console.log('數據傳遞', this.printMess);
    },
    methods: {
      getTime(){
        this.$http.post('/pub/getTime',{type:'yyyy-MM-dd'}).then(res=>{
          if(res.code == 200){
            this.time = res.result
          }
        })
      },
      getNum(){
          this.$http.post('/api/lcwxjl/getPjbh').then((res)=>{
              if (res.code== 200){
                  this.num = res.message
              }else {

              }
          })
        // var v = this
        // for(var item of this.printMess){
        //   console.log("log " , item.chargeRecord.pjbh);
        //   if(item.chargeRecord.pjbh){
        //     let arr = item.chargeRecord.pjbh.split('-');
        //     v.num = arr[0] + '-' + arr[1];
        //     return
        //   }
        // }
        // setTimeout(()=>{
        //   v.AF.getPrintNum('signUp',this.studentIDS,num => {
        //     v.num = num
        //     console.log("num -> " , num);
        //     // v.num = 0
        //     if(v.num == 0){
        //       v.num = '网络异常!请点击取消按钮,重新打开获取票据号';
        //       swal({
        //         title: '网络异常!请点击取消按钮,重新打开获取票据号',
        //         type: 'warning',
        //         confirmButtonText: '确定'
        //       })
        //     }
        //   })
        // },100)
      },
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
        this.BDNum()
        this.bzShow = false
        const cssText = `
          #printDivSigUp {
            font-family: "黑体";
            color: #000000!important;
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
      width: 12mm;

    }
    .LiftTitItem {
        height: 13mm;
        line-height: 13mm;
      }
    .rightTitSize {
      font-size: 12px;
      padding: 35px 8px 8px 0;
      float: right;
      height: 70mm;
      width: 10mm;

    }
    .rightTitItem {
        height: 5mm;
        line-height: 3mm;
      }
       .titMess {
        font-size: 16px;
        height: 10mm;
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
            width: 130mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 19px;
            padding-right: 4px;
            padding-left: 6px;
            margin-bottom: 3px;
          }
          .messTit2 {
            font-size: 18px;
            width: 20mm;
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
          .messTit3 {
            font-size: 16px;
            width: 30mm;
            text-align: right;
            float: left;
          }
          .ItemMess3 {
            float: left;
            width: 60mm;
            border-bottom: solid 1px #000;
            text-align: left;
            font-size: 18px;
          }
    .pageBox {
      float: left;
      width: 190mm;
      /**/


      }
      .messList {
          overflow: hidden;
          line-height: 10mm;


        }
        .messTit11 {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess11 {
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
          .messTit211 {
            font-size: 16px;
            width: 25mm;
            text-align: right;
            float: left;
          }
          .ItemMess211 {
            float: left;
            width: 20mm;
            /*border-bottom: solid 1px #000;*/
            text-align: left;
            font-size: 18px;
          }
      .boxmess {
        border: solid 1px #000;
        padding-right: 8px;
        min-height: 60mm;
       }
      .boxmess1 {
        padding-right: 8px;

      }
      .messList11 {
          overflow: hidden;
          line-height: 10mm;

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
