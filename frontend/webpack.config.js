/* eslint no-console: 0, no-sync: 0, no-process-env: 0, no-extra-parens: 0 */

const childProcess = require('child_process');
const path = require('path');

const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CreateFileWebpackPlugin = require('create-file-webpack');

const { UnusedFilesWebpackPlugin } = require('unused-files-webpack-plugin');
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer');

const projectName = 'WebAuthn demo';

const SRC_DIR = path.resolve(__dirname, 'src');
const BUILD_DIR = path.resolve(__dirname, 'build');
const DIST_DIR = path.resolve(BUILD_DIR, 'dist');

const devConfig = {
  bail: false,
  devtool: 'cheap-module-eval-source-map',

  devServer: {
    historyApiFallback: true,
    https: true,
    public: 'demo.yubico.test',
    proxy: {
      '/api': {
        target: 'https://demo.yubico.test:8443',
        secure: false,
      },
    },
  },
};

const devPlugins = [new webpack.NamedModulesPlugin()];

const prodConfig = {
  devtool: '',
};

const prodPlugins = [
  new UnusedFilesWebpackPlugin(),
  new BundleAnalyzerPlugin({
    analyzerMode: 'disabled',
    generateStatsFile: false,
  }),
];

module.exports = (env, argv) => {
  const IS_PROD = argv.mode === 'production' ? true : false;

  return {
    context: SRC_DIR,

    entry: IS_PROD ? 'index.prod' : 'index.dev',

    output: {
      path: DIST_DIR,
      filename: IS_PROD ? '[name].[contenthash].js' : '[name].[hash].js',
      publicPath: '/',
    },

    resolve: {
      extensions: ['.js', '.jsx'],

      modules: [SRC_DIR, 'node_modules'],
    },

    module: {
      rules: [
        {
          test: /\.jsx?$/,
          enforce: 'pre',
          exclude: /node_modules/,
          loader: 'eslint-loader',
          options: {
            quiet: true,
          },
        },

        {
          test: /\.jsx?$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
          },
        },

        {
          test: /\.css$/,
          use: ['style-loader', 'css-loader'],
        },

        {
          test: [
            /\.eot$/,
            /\.png$/,
            /\.jpg$/,
            /\.svg$/,
            /\.ttf$/,
            /\.woff$/,
            /\.woff2$/,
            /\.otf$/,
          ],
          use: {
            loader: 'file-loader',
          },
        },
      ],
    },

    plugins: [
      new HtmlWebpackPlugin({
        title: projectName,
        meta: {
          viewport:
            'minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no',
        },
      }),
      ...(IS_PROD ? prodPlugins : devPlugins),
    ],

    ...(IS_PROD ? prodConfig : devConfig),

    optimization: {
      splitChunks: {},
    },
  };
};
