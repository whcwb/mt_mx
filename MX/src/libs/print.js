import $http from '@/axios/index'
import swal from 'sweetalert2';

var obj = {}

obj.print = (id,item,time,callback) => {
  var printJson = [
    {'x': 10, 'y': 30, 'w': 250, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '车辆租赁凭据', 'font_size': 12},
    {'x': 10, 'y': 60, 'w': 250, 'h': 80, 'isbar': 'T', 'border': 1, 'text': id, 'font_size': 15},
    {'x': 10, 'y': 140, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '训练科目', 'font_size': 12},
    {'x': 110, 'y': 140, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '科目二', 'font_size': 9},
    {'x': 10, 'y': 170, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '车辆编号', 'font_size': 12},
    {'x': 110, 'y': 170, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 9},
    {'x': 10, 'y': 200, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '训练车型', 'font_size': 12},
    {'x': 110, 'y': 200, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 9},
    {'x': 10, 'y': 230, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '教练员', 'font_size': 12},
    {'x': 110, 'y': 230, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '张平', 'font_size': 9},
    {'x': 10, 'y': 260, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '安全员', 'font_size': 12},
    {'x': 110, 'y': 260, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 9},
    {'x': 10, 'y': 290, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '累计时长', 'font_size': 12},
    {'x': 110, 'y': 290, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '', 'font_size': 9},
    {'x': 10, 'y': 320, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '累计费用', 'font_size': 12},
    {'x': 110, 'y': 320, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '300元', 'font_size': 9},
    {'x': 10, 'y': 350, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '备注', 'font_size': 12},
    {'x': 110, 'y': 350, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': '1人', 'font_size': 9},
    {'x': 10, 'y': 380, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 0, 'text': '本票据遗失不补', 'font_size': 9},
    {'x': 125, 'y': 380, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 0, 'text': time, 'font_size': 9}
  ];

  let itemIndex=0;
  printJson.map((val,index,arr)=>{
    if(index>2&&index%2!==0&&index<18){
      val.text=item[itemIndex]
      itemIndex++;
    }
  })

  console.log(item)
  console.log(printJson,'sssss')


  item.map((val,index,arr)=>{
    if(index===item.length-1){               //若备注超出13个字符，以,分隔，按每行两个人处理
      // if(val.length)
      if(val.length>14){
        let arr=val.split(',')
        let arr1=[]
        for (let i=0;i<arr.length/2+2;i=i+2){
          let arr2=[]
          for (let a=0;a<2;a++){
            arr2.push(arr[i+a])
          }
          arr1.push(arr2.join(','))
          // console.log(arr2)
        }
        if(arr.length%2!=0&&arr.length!==3){
          arr1.push(arr[arr.length-1])
        }

        console.log(arr1)

        printJson.splice(16, 4)

        var firstY=320

        for (let c=0;c<arr1.length;c++){
          let bz=c==0?'备注':'备注'+c
          let fontsize=c==0?12:9
          let gz={'x': 10, 'y': firstY+30, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 1, 'text': bz, 'font_size': fontsize}
            printJson.push(gz)
          let obj={'x': 110, 'y': firstY+30, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 1, 'text': arr1[c], 'font_size': 9}
            printJson.push(obj)
          firstY+=30
        }

        let textObj1={'x': 10, 'y': 380, 'w': 100, 'h': 30, 'isbar': 'F', 'border': 0, 'text': '本票据遗失不补', 'font_size': 9}
        let textObj2={'x': 125, 'y': 380, 'w': 150, 'h': 30, 'isbar': 'F', 'border': 0, 'text': time, 'font_size': 9}

        textObj1.y=textObj2.y=printJson[printJson.length-1].y+30

        printJson.push(textObj1)
        printJson.push(textObj2)

        console.log(printJson)
      }

    }
  })

  // return

  $.ajax({
    type: "POST",
    url: "http://127.0.0.1:39999/print",
    data: JSON.stringify(printJson),
    contentType: "application/json",
    dataType: "json",
    beforeSend: function () { },
    success: function (data) {
      // alert("打印成功="+data);
    },
    error:function(data){
      // alert("服务未启动，请先启动监听服务");
    }
  });

}

export default obj

