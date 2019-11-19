<template>
  <div>
    <Modal
      v-model="showModal"
      height="200"
      width="600"
      :closable='false'
      :mask-closable="false"
      :title="'修改绑定卡片'">
      <Form
        ref="form"
        :rules="ruleInline"
        :label-width="100"
        :styles="{top: '20px'}">
        <div style="overflow: auto;height: 100px;width:500px">
          <Row>
            <Col span="20">
              <FormItem label="卡片绑定">
                <Input v-model="cardNo" size="large" readonly>
                  <span slot="append" style="cursor: pointer" @click="getCardNum">读卡</span>
                </Input>
              </FormItem>
            </Col>
          </Row>
        </div>
      </Form>
      <div slot='footer'>
        <Button type="default" @click="close" style="color: #949494">取消</Button>
        <Button type="primary" @click="GXCar">确定</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
  import upImg from '../../../../components/Upload/UploadImg'
  import readCard from '../../comp/readCard'
  export default {
    name: '',
    components:{
      upImg
    },
    props:{
      param: {
        type: Object,
        default: {},
        clImg: 'carImg'
      }
    },
    data() {
      return {
        params:{
          cardNo:''
        },
        cardNo:'',
        ZT:[],
        CX:[],
        KM: [
          {
            value: '2',
            label: '科目二'
          },
          {
            value: '3',
            label: '科目三'
          }
        ],
        v: this,
        showModal: true,
        operate: "修改",
        editMode: false,
        //新增数据
        showPsd: false,
        ruleInline: {},
        chCarNum:{
          id:'',
          cardNo:'',
          th:''
        }
      }
    },
    created() {
      this.params = JSON.parse(JSON.stringify(this.param))
      this.param.id = this.params.id
      this.chCarNum.id = this.params.id
      if(this.param.cardNo){
        this.cardNo = this.param.cardNo.substring(0,2)+'******'
      }
      this.getCLZT()
    },
    methods: {
      getCardNum(){
        // readCard.getCardNum((key,res)=>{
        //   if(key){
        //     this.chCarNum.cardNo = res
        //     this.changeNum(res)
        //   }else {
        //     this.swal({
        //       title:'请重新放置卡片',
        //       type:'error'
        //     })
        //   }
        // })

        readCard.readCardChrome((key,res)=>{
          if(res!=='None'){
            this.chCarNum.cardNo = res
            this.changeNum(res)
          }else {
            this.swal({
              title:'请重新放置卡片',
              type:'error'
            })
          }
        })
      },
      changeNum(num){
        var v = this
        this.$http.post('/api/lccl/updateCardNo',this.chCarNum).then(res=>{
          if (res.code == 200){
            this.params.cardNo = num
            this.cardNo = num.substring(0,2)+ '******'
            v.swal({
              title:res.message,
              type:'warning',
              confirmButtonText: '确认',
            })
          }else if(res.code == 505){
            this.swal({
              title:res.message,
              type:'warning',
              showCancelButton: true,
              confirmButtonText: '替换',
              cancelButtonText: '取消',
            }).then((p)=>{
              if(p.value){
                v.chCarNum.th = '****'
                v.changeNum(num)
              }
            })
          }else {
            this.swal({
              title:res.message,
              type:'error'
            })
          }
        }).catch((err)=>{})
      },
      txImg(A, url){
        this.param[A] = url
      },
      getCLZT(){
        this.ZT = this.dictUtil.getByCode(this,'ZDCLK1044');
        for (var i=0; i<this.ZT.length; i++) {
            let val = this.ZT[i]
            if(val.key === '01'){
              this.ZT.splice(i,1)
              break
          }
        }
        this.CX = this.dictUtil.getByCode(this,'ZDCLK0040');
      },
      close(){
        this.$parent.compName = '';
        this.$parent.getPagerList()
      },
      GXCar(){
        delete this.params.lcJl;
        delete this.params.zdxm;
        this.$http.post(this.apis.CLWH.CLGX,this.params).then(res=>{
          if (res.code == 200){
            this.$Message.info(res.message);
            // this.showModal = false;
            this.$parent.compName = '';
            this.$parent.getPagerList();
          }else {
            this.swal({
              title:res.message,
              type:'warning',
              showCancelButton: false,
              confirmButtonText: '确定',
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
