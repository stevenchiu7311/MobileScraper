document.documentElement.addEventListener("click", function(e) {
    const path = cssPath(e.target);
     console.log("path:"+path);
     $(path).each(function(index, value){
        console.log("$(this):"+index);
        //value.style.background = 'blue';
     });
});