const path = require('path');

module.exports = env => ({
    entry: './src/main/js/app.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    output: {
        path: __dirname + (env && env.production ? '/src/main/' : '/out/production/') + 'resources/static/',
        filename: 'bundle.js',
    },
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