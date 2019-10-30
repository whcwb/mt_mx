<template>
  <div class="box_col" style="margin-left: 20px;">
    <pager-tit title="费用修改"></pager-tit>
    <div class="box_col_auto" style="overflow-x: hidden;">
      <Row :gutter="12">
        <Col span="8" v-for="(item,index) in list" :prop="item.zdmc" :key="item.zdId" >
          <Card  style="margin-top: 12px;">
            <p slot="title">
              <Icon type="ios-car"></Icon>
              {{item.by1 +'-' + item.by2}}
            </p>
            <Row>
              <Col span="20">
                <InputNumber  v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 200px;" @on-change="change(item)"></InputNumber>
                <span>元</span>
              </Col>
              <Col span="4">

              </Col>
            </Row>
            <Row  style="margin-top: 16px;">
              <Col span="20">
                <InputNumber  v-model="item.by3" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span v-if="item.zddm =='K2PY'"> 元/人</span>
                <span v-else-if="item.zddm =='K2KF1'||item.zddm =='K2KF2'||item.zddm =='K2KF3'"> 元</span>
                <span v-else> 元/分钟</span>
              </Col>
              <Col span="4">
              </Col>
            </Row>
            <Row  style="margin-top: 16px;">
              <Col span="20">
                <InputNumber  v-model="item.by4" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span>返点率</span>
              </Col>
              <Col span="4">
                <Button type="primary" @click="confirm(item)">修改</Button>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  </div>
</template>

<script>
  export default {
    name: "index",
    data(){
      return{
          v:this,
          formItem:{},
          ruleInline:{},
          formItemGroup:[],
          list:[]
      }
    },
    methods: {
        change(item){
            item.by3 = parseInt(item.zdmc / 60);
        },
        confirm(item){
          this.$http.post(this.apis.DICTIONARY_LIST.CHANGE,item).then((res)=>{
              if (res.code == 200){
                  this.$Message.success(res.message);
              }else{
                  this.$Messgae.error(res.message);
              }
          })
        },
        getData(){
            let param = {
                zdlmdm:'ZDCLK1045'
            }
            this.$http.get(this.apis.DICTIONARY_LIST.list,{params:param}).then((res)=>{
                if (res.code == 200 && res.result){
                    this.list = res.result
                    for (let r of this.list){
                        r.editMode = false
                        r.zdmc = parseInt(r.zdmc)
                        r.by3 = parseFloat(r.by3)
                        r.by4 = parseFloat(r.by4)
                        if(r.zddm =='k2JS'){
                            r.by2 = r.by2+'-计时'
                        } if(r.zddm =='K2PY'){
                            r.by2 = r.by2+'-培优'
                        }if(r.zddm =='K2KF1'){
                            r.by2 = r.by2+'-开放日1'
                        }if(r.zddm =='K2KF2'){
                            r.by2 = r.by2+'-开放日2'
                        }if(r.zddm =='K2KF3'){
                            r.by2 = r.by2+'-开放日3'
                        }
                    }
                }else{
                    this.$Message.error(res.message)
                }
            })
        }
    },
    mounted () {

    },
    created(){
        this.getData();
    }
  }
</script>

<style scoped>
</style>
