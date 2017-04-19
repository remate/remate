var htmlWebpackPlugin = require('html-webpack-plugin');
module.exports = {
    // configuration
    entry:'./src/app.js',
    output:{
        path:__dirname + "/dist",
        filename:'js/[name].bundle.js'//chunkhash可以保持唯一性
        // publicPath:'http://cnd.com'
    },
    module:{
        loaders:[
            {
                test:/\.js$/,
                loader:'babel-loader',
                exclude: /node_modules/,//用正则
                include:/src/,
                options:{
                    "presets": ["latest"]//解析js根据不同特性

                }   //或者在package.json中定义
            }
        ]
    },
    plugins:[
        new htmlWebpackPlugin({
            // filename:'index-[hash].html',
            filename:'index.html',
            template:'index.html',
            // inject:'head',
            title:'webpack is good',
            minify:{
                removeComments:false,//true为去除注释
                collapseWhitespace:false  //去除页面空格
            }
        })
    ]
};