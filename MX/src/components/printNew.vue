<template>
  <div>
    <Modal
      v-model="showModal"
      :closable='false'
      :mask-closable="false"
      :footer-hide="true"
      class-name="vertical-center-modal">

      <div slot="header">
        <Row>
          <!--<Col span="4 ">-->
            <!--<Col span="4">-->
            <!--            <p>车单打印</p>-->
          <!--</Col>-->
          <!--<Col span="4" align="center">
            <span v-if="printClose">
            {{ mesTime }}s后关闭
            </span>
          </Col>-->
          <Col span="24" align="center">
            <Button type="default" size="large" style="margin: 0 12px"
                    @click="close">关闭
            </Button>
            <!--<Button type="success" size="large" style="margin: 0 12px"-->
                    <!--@click="doPrint('打印预览')">打印预览-->
            <!--</Button>-->
            <Button type="success" size="large" :disabled="isDone"
                    @click="doPrint('')">打印
            </Button>
          </Col>
        </Row>
      </div>
      <div class="printSty" ref="printDiv">
        <!--        <div style="font-weight: 500;font-size: 16px">{{info.jssj}}</div>-->
        <table border="1" cellpadding="0" cellspacing="0" style="width: 100%;font-family: 黑体;color:#000">
          <tr>
            <td colspan="2" style="text-align: center"><span style="font-size: 20px;">车辆租赁凭据</span></td>
          </tr>
          <tr>
            <td colspan="2">
              <div>
                <barcode id="barcode" style="top:500px;position:absolute;top:-500px;opacity:0"
                         :value="info.id" :options="{ displayValue: false,height:40}"></barcode>
              </div>
              <div>
                <div id="barcode1"><img :src="codeSrc" alt="" style="width: 100%"></div>
              </div>
              <div style="text-align: center"><span>{{info.id}}</span></div>
            </td>
          </tr>
          <tr v-for="(item,index) in displayItem" :key="index">
            <td>{{item.title}}</td>
            <td>
              {{item.val}}
              <!--{{getDict(info[item.key],item.dict)}}-->
              <!--<span v-if="item.key2">{{info[item.key2]}}</span>-->
              <!--<span v-if="item.unit">{{item.unit}}2</span>-->
            </td>
          </tr>
        </table>
        <div>
          <Row>
            <Col span="12">
              <div style="font-family:黑体;font-weight: 600;font-size: 12px!important;color: #000">本票据遗失不补</div>
            </Col>
            <Col>
              <div style="font-weight: 500;font-size: 12px;text-align: right">{{info.jssj}}</div>
            </Col>
          </Row>
        </div>

      </div>
    </Modal>
  </div>
</template>

<script>
  import {Printd} from 'printd'
  import barcode from '@xkeshi/vue-barcode';
  import {mapMutations} from 'vuex'
  import print from '../libs/print'

  export default {
    components: {barcode},
    name: "print",
    props: {
      hisPrintMess: {
        type: Object,
        default: () => {
          return {}
        }
      },
      printClose: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        showModal: true,
        showCode: false,
        codeSrc: '',
        isDone:true,
        displayItem:{},
        mess: [
          // {title: '训练科目', key: 'km'},
          {title: '训练科目', key: 'zddm', dict: 'ZDCLK1045'},
          {title: '车辆编号', key: 'clBh'},
          {title: '训练车型', key: 'jlCx'},
          {title: '教练员', key: 'jlJx', key2: 'jlXm'},
          {title: '安全员', key: 'zgXm'},
          // {title: '优惠时长', key: 'yhsc'},
          // {title: '优惠金额', key: 'yhje'},
          {title: '累计时长', key: 'sc'},
          {title: '累计费用', key: 'lcFy'},
          {title: '备注', key: 'bz'},

        ],
        mesTime: 7,
        info: '',
        Interval: -1
      }
    },
    created() {
      this.info = JSON.parse(JSON.stringify(this.hisPrintMess))
      console.log(this.info, 'fsdf')
      console.log(this.info)
      // if (this.info.pz != '') {
        this.$http.post('/api/lcjl/getByPz', {pz: this.info.pz}).then((res) => {
          if (res.code == 200) {
            this.info = res.result
            this.info.sc=this.info.sc=='0'?'': this.info.sc+'分钟'
            if (this.info.fdr.indexOf('1') >= 0) {
              this.info.lcFy = ''
            }
            if (this.info.lcFy == '') {
              this.info.lcFy = ''
            } else {
              this.info.lcFy = this.info.lcFy + '元'
            }

            if(this.info.lcFy == 0) this.info.lcFy='0'

            this.displayItem = [
              {
                title:'训练科目',
                val:this.info.zdmc
              },
              {
                title:'车辆编号',
                val:this.info.clBh,
              },
              {
                title:'训练车型',
                val:this.info.jlCx,
              },
              {
                title:'教练员',
                val:this.info.jlJx+' '+this.info.jlXm,
              },
              {
                title:'安全员',
                val:this.info.zgXm
              },
              {
                title:'累计时长',
                val:this.info.sc,
              },
              {
                title:'累计费用',
                val:this.info.lcFy,
              },
              {
                title:'备注',
                val:this.info.bz,
              },
            ]

            if (this.printClose) {
              this.doPrint('')
              this.showModal=false
            }

            this.isDone=false
          }
        })
      // }
      // else {
      //   this.info.sc = this.parseTime(this.info.sc)
      //   // this.info.kssj = this.info.kssj.substr(0, 16)
      //   this.info.jssj = this.info.jssj.substring(0, 16)
      //   if (this.info.fdr.indexOf('1') >= 0) {
      //     this.info.lcFy = ''
      //   }
      //   if (this.info.lcLx == '20') {
      //     this.info.bz = this.info.xyXm + "-" + this.info.xyDh
      //   }
      //   // if(this.info.lcLx == '00' && (this.info.cardje - this.info.lcFy)>=0){
      //   //     this.info.bz = this.info.bz + ' 元,余额'+(this.info.cardje-this.info.lcFy) +' 元'
      //   // }else {
      //   //     this.info.bz = this.info.bz + '元'
      //   // }
      //   if (this.info.lcFy != '' || this.info.lcFy == 0) {
      //     this.info.lcFy = this.info.lcFy + '元'
      //   } else {
      //     this.info.lcFy = ''
      //   }
      // }
      if (this.info.lcFy == '元') {
        this.info.lcFy = " "
      }

      let v = this;
      setTimeout(() => {
        let canvas = document.getElementById("barcode");
        if(this.showModal) {
          v.codeSrc = canvas.toDataURL()
        setTimeout(() => {
          this.SetPprintInnerHTML(this.$refs.printDiv.innerHTML)
        }, 300)}
      }, 200)
      // this.enter()
    },
    beforeDestroy() {
      this.SetPprintInnerHTML('')
    },
    mounted() {
      var v = this
      this.$nextTick(() => {
        // document.getElementById("page1").innerHTML = this.$refs.printDiv.innerHTML;



          // this.Interval = setInterval(() => {
          //   this.mesTime--
          //   if (this.mesTime <= 0){
          //     clearInterval(this.Interval)
          //   }
          // }, 1000)
          //
          // setTimeout(() => {
          //   this.doPrint('', () => {
          //     setTimeout(() => {
          //       v.close()
          //     }, 5000)
          //   })
          // }, 1000)

      })
    },
    methods: {
      ...mapMutations([
        'SetPprintInnerHTML'
      ]),
      enter() {
        var _this = this;
        document.onkeydown = function (e) {
          let key = window.event.keyCode;
          if (key == 13) {
            _this.doPrint();
          }
        };
      },
      doPrint(how, callback) {
        let item=[]
        this.displayItem.map((val, index, arr)=>{
          item[index]=val.val
        })

        print.print(this.info.id,item, this.info.jssj.substring(0, 16))

        this.close()

      },
      parseTime(s) {
        s = parseInt(s);
        let h = parseInt(s / 60);
        let m = s % 60;
        let r = '';
        if (h != 0) r += h + '小时'
        return r + m + '分钟'
      },
      getDict(val, key) {
        if (key) {
          return this.dictUtil.getValByCode(this, key, val)
        }
        return val
      },
      close() {
        // this.$parent.compHisName = '';
        // document.getElementById("page1").innerHTML = "";
        // console.log('q');
        clearInterval(this.Interval)
        this.$parent.componentName = ''
        if (typeof this.$parent.getCarList == 'function') {
          this.$parent.getCarList()
        }
      }
    }
  }
</script>

<style lang="less">
  #page1 {
    width: 68mm;
    margin: auto;
  }

  .printSty {
    width: 100%;
    /*background-color: #ed3f14;*/
    font-size: 18px;
    font-weight: 600;
  }

  td {
    padding: 5px 2px;
    text-align: center;
  }
</style>
