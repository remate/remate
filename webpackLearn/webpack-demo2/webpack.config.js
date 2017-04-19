module.exports = {
    // devtool:'source-map',
    entry:{
        main:__dirname + '/app/main.js'
    },
    output:{
        path:__dirname + '/public',
        filename:'[name].bundle.js'
    }
}