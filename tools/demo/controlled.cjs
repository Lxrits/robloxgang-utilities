const mineflayer=require('mineflayer'),fs=require('fs');
const names=['IlyaRobloxPro52','Lxrits','AndreyRobloxFan','TimuRobloxLOL52','VladRobloxSHACAL'];
const bots=[];let ready=false;fs.writeFileSync('controlled.pid',String(process.pid));
function connect(i){
 const b=mineflayer.createBot({host:'127.0.0.1',port:25575,username:names[i],auth:'offline',version:'1.21.9'});bots.push(b);
 b.on('error',e=>console.log('ERROR',names[i],e.message));
 b.once('spawn',()=>{console.log('READY',names[i]);if(i<4)setTimeout(()=>connect(i+1),500);else {ready=true;try{previous=fs.readFileSync('commands.json','utf8')}catch{}fs.writeFileSync('controlled-ready.txt',names.join('\n'))}});
 b.on('kicked',r=>console.log('KICKED',names[i],r));
}
connect(0);
let previous='';
setInterval(()=>{
 if(!ready)return;
 let value;try{value=fs.readFileSync('commands.json','utf8')}catch{return}
 if(value===previous)return;previous=value;
 let commands;try{commands=JSON.parse(value)}catch{return}
 if(commands?.stop===true){bots.forEach(b=>b.quit());setTimeout(()=>process.exit(0),500);return}
 if(!Array.isArray(commands))return;
 commands.filter(v=>typeof v==='string').forEach((message,mi)=>bots.forEach((b,bi)=>setTimeout(()=>b.chat(message),(mi*5+bi)*350)));
},250);
