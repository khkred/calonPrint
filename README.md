# calonPrint

An innovative app that reads data from various scales using Bluetooth Low Energy (BLE) and prints receipts using point-of-sale (POS) systems. This project includes multiple branches supporting different POS SDKs, including **MSwipe** and **Sunmi**.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Branches](#branches)
- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [Dependencies](#dependencies)
- [Usage](#usage)
  - [BLE Data Reading](#ble-data-reading)
  - [Printing Receipts](#printing-receipts)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Overview

**calonPrint** enables seamless integration with BLE scales to collect weight or measurement data and print receipts directly via POS systems. Designed for ease of use in various retail and industrial environments, this app automates data collection from BLE-enabled scales and offers fast, efficient receipt printing.

## Features

- **Bluetooth Low Energy (BLE) Integration**: Reads data from a range of BLE-enabled scales, providing a streamlined and hands-free data collection process.
- **POS Receipt Printing**: Supports receipt printing with two SDK options:
  - **MSwipe POS SDK** (in `mswipe` branch)
  - **Sunmi POS SDK** (in `main2` branch)
- **Real-Time Data Collection**: Automatically captures data from the connected BLE devices for immediate processing.
- **Customizable Print Layouts**: Offers customizable receipt layouts, meeting various business requirements.

## Branches

- **`main2` Branch**: Uses the Sunmi SDK for receipt printing.
- **`mswipe` Branch**: Utilizes the MSwipe POS SDK for receipt printing.

Each branch is tailored for the respective POS SDK, allowing flexibility in deployment based on available hardware.

## Getting Started

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/khkred/calonPrint.git
   ```
2. **Checkout the Desired Branch**:
   - For Sunmi SDK: 
     ```bash
     git checkout main2
     ```
   - For MSwipe SDK:
     ```bash
     git checkout mswipe
     ```

### Dependencies

Ensure you have the following dependencies installed:
- Android Studio (for Android development)
- BLE libraries for Flutter/Android
- Sunmi or MSwipe SDK (depending on the branch)

## Usage

### BLE Data Reading

1. **Enable BLE on Device**: Ensure Bluetooth is enabled on your device.
2. **Connect to Scale**: The app will automatically search for nearby BLE scales and prompt a connection. Ensure the scale is powered on and within range.
3. **Data Capture**: Once connected, the app will capture real-time weight or measurement data, displaying it on the app interface.

### Printing Receipts

1. **Prepare Receipt Data**: After capturing data from the scale, select the print option.
2. **Print via POS**:
   - **Sunmi SDK** (main2 branch): The app sends data to the Sunmi POS device, utilizing its in-built receipt printer.
   - **MSwipe SDK** (mswipe branch): The app uses MSwipe’s SDK for connected POS systems to print the receipt.

## Configuration

- **BLE Settings**: Adjust BLE connection intervals, timeout, and other settings in the app’s BLE configuration file for optimal performance.
- **Receipt Layout**: Customize receipt headers, footers, and data format in the configuration file to align with branding or data requirements.
