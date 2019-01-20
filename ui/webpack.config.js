var path = require('path');

module.exports = {
    entry: './src/main/app/index.js',
    devtool: 'sourcemaps',
    cache: true,
    debug: true,
    output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js'
    },
    module: {
        loaders: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                loader: 'babel',
                query: {
                    cacheDirectory: true,
                    presets: ['es2015', 'stage-0', 'react']
                }
            },
            {test: /\.css$/, loader: 'style-loader!css-loader'},
            {test: /(\.eot|\.woff2|\.woff|\.ttf|\.svg)/, loader: 'file-loader'}
        ]
    }
};