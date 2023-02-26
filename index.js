import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';

import CodePush from "react-native-code-push";

export default class mitraRn extends Component {

  constructor() {
    super();
    this.state = { restartAllowed: true };
  }

  codePushStatusDidChange(syncStatus) {
    switch(syncStatus) {
      case CodePush.SyncStatus.CHECKING_FOR_UPDATE:
        this.setState({ syncMessage: "Checking for update." });
        break;
      case CodePush.SyncStatus.DOWNLOADING_PACKAGE:
        this.setState({ syncMessage: "Downloading package." });
        break;
      case CodePush.SyncStatus.AWAITING_USER_ACTION:
        this.setState({ syncMessage: "Awaiting user action." });
        break;
      case CodePush.SyncStatus.INSTALLING_UPDATE:
        this.setState({ syncMessage: "Installing update." });
        break;
      case CodePush.SyncStatus.UP_TO_DATE:
        this.setState({ syncMessage: "App up to date.", progress: false });
        break;
      case CodePush.SyncStatus.UPDATE_IGNORED:
        this.setState({ syncMessage: "Update cancelled by user.", progress: false });
        break;
      case CodePush.SyncStatus.UPDATE_INSTALLED:
        this.setState({ syncMessage: "Update installed and will be applied on restart.", progress: false });
        break;
      case CodePush.SyncStatus.UNKNOWN_ERROR:
        this.setState({ syncMessage: "An unknown error occurred.", progress: false });
        break;
    }
  }

  /** Update is downloaded silently, and applied on restart (recommended) */
  sync() {
    CodePush.sync(
      {},
      this.codePushStatusDidChange.bind(this),
      null
    );
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native for Mitra!
        </Text>
        <TouchableOpacity onPress={this.sync.bind(this)}>
          <Text style={styles.syncButton}>Press for background sync</Text>
        </TouchableOpacity>
        <Text style={styles.instructions}>
          To get started, edit index.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu 222
        </Text>
        <Text style={styles.messages}>{this.state.syncMessage || ""}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

let codePushOptions = { checkFrequency: CodePush.CheckFrequency.ON_APP_RESUME };
mitraRn = CodePush(codePushOptions)(mitraRn);

AppRegistry.registerComponent('mitra-rn-poc', () => mitraRn);
