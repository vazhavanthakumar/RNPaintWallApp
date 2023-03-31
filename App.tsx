/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  SafeAreaView,
  Text,
  TouchableOpacity,
  NativeModules,
} from 'react-native';

function App(): JSX.Element {
  var openCVAndroidModule = NativeModules.OpenCVAndroidModule;

  const navigateToWallPaintActivity = () => {
    console.log('onPressedEventTriggered');
    openCVAndroidModule.openWallPaintActivity();
  };

  return (
    <SafeAreaView
      style={{
        width: '100%',
        height: '100%',
        justifyContent: 'center',
      }}>
      <TouchableOpacity
        style={{
          backgroundColor: 'darksalmon',
          alignSelf: 'center',
          padding: 12,
          borderRadius: 12,
        }}
        onPress={() => navigateToWallPaintActivity()}>
        <Text
          style={{
            color: 'white',
          }}>
          Navigate to Wall Paint Activity
        </Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}

export default App;
