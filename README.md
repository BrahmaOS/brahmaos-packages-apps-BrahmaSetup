# BrahmaSetup


Feature
------
BrahmaSetup is the Android Setup Wizard app for Brahma OS. 

It will guide users to CREATE or IMPORT a decentralized Brahmaos Account as the user's unique identity. In the same time, it will generated unique PRIVATE/PUBLIC key and each WALLET's Default Account by the unique mnemonics. 

Brahmaos Account
------
The Brahmaos Account is an unique identity for the user. It is generated by follow encryption algorithm:
> Brahmaos Account = SHA256(AES128(Mnemonics, Password))

We save the Brahmaos Account to local and support interface in **UserManager** for other apps getting the unique Brahmaos Account.

Default Wallet Accounts
------
The Default Wallet Accounts(of BRM, ETC, BTC...) are generated by the same Mnemonics of Brahmaos Account and can't be removed from the wallet app.

We save the **Wallet Accounts' addresses** to local and support interface in **UserManager** for wallet app getting address for each wallet account.

Also we save **encrypted mnemonics hex** to local and support interface in **UserManager** for wallet app getting mnemonics by the right password input by the USER. The way to encrypt mnemonics is:
> hex(AES128(Mnemonics, password))


Private & Public Key
------
This key pair is unique and generated by the same Mnemonics of Brahmaos Account. This unique key pair is for data encrypt/decrypt in Brahma OS.

We save **public key** and **encrypted private key** to local and support interface in **UserManager** for FileManager geting public key and private key by the right password input by the USER. The way to encrypt private key is:
> hex(AES128(privatekey, password))


Building
------
This app's codes used the self-defined interfaces added in the Brahma OS framework. Before building this app, you need to pull and apply the framework PATCH under BrahmaOS/brahmaos-patches <https://github.com/BrahmaOS/brahmaos-patches/commit/b07db7d1516818ecc7c78b3e74094bf12340bfe5>.

