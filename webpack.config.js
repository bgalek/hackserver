const path = require('path');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = env => ({
    entry: './src/main/js/app.js',
    devtool: 'source-map',
    cache: true,
    mode: 'development',
    output: {
        path: __dirname + (env && env.production ? '/src/main/' : '/out/production/') + 'resources/static/',
        filename: 'bundle.js',
    },
    plugins: [
        new CleanWebpackPlugin({cleanOnceBeforeBuildPatterns: ['!*.html', '!*.css']}),
    ],
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"],
                        plugins: ["@babel/plugin-proposal-class-properties", "@babel/plugin-transform-runtime"]
                    }
                }]
            }
        ]
    }
});