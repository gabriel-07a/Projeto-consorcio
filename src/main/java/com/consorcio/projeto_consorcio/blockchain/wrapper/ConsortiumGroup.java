package com.consorcio.projeto_consorcio.blockchain.wrapper;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.10.3.
 */
@SuppressWarnings("rawtypes")
public class ConsortiumGroup extends Contract {
    public static final String BINARY = "610100604052348015610010575f5ffd5b506040516123ad3803806123ad83398101604081905261002f916101a9565b60017f9b779b17422d0df92223018b32b4d1fa46e071723d6817e2486d003becc55f00556001600160a01b03851660805260a084905260c083905260e0829052600b81905561007e5f886100e5565b506100a97f25cf2b509f2a7f322675b2a5322b182f44ad2c03ac941a0af17c9b178f5d5d5f876100e5565b50600160078190556040517f16ae2f9b3cb8b3312b810271924b6bece77a7bbedb1d827098eb6d0ee48e20fa905f90a250505050505050610210565b5f828152602081815260408083206001600160a01b038516845290915281205460ff16610185575f838152602081815260408083206001600160a01b03861684529091529020805460ff1916600117905561013d3390565b6001600160a01b0316826001600160a01b0316847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a4506001610188565b505f5b92915050565b80516001600160a01b03811681146101a4575f5ffd5b919050565b5f5f5f5f5f5f5f60e0888a0312156101bf575f5ffd5b6101c88861018e565b96506101d66020890161018e565b95506101e46040890161018e565b606089015160808a015160a08b015160c0909b0151999c989b5091999098919790965090945092505050565b60805160a05160c05160e0516121086102a55f395f81816105b0015261189801525f8181610543015281816119b7015281816119dd0152611a2f01525f818161048c015281816113ba0152818161158b015281816115e7015281816116da015261172e01525f81816105f30152818161084801528181610bc201528181610e53015281816116b7015261199301526121085ff3fe608060405234801561000f575f5ffd5b5060043610610234575f3560e01c806369a8c5f211610135578063b43976a7116100b4578063c3cd31c711610079578063c3cd31c7146105ab578063cf99e48d146105d2578063d547741f146105db578063e9cbd822146105ee578063e9e1965d1461062d575f5ffd5b8063b43976a714610565578063b81522a41461056d578063bab2f55214610576578063be0b45b71461057f578063bef4876b1461059e575f5ffd5b806392c2becc116100fa57806392c2becc14610511578063a217fddf14610525578063a6e352761461052c578063b052e96e14610535578063b199e7551461053e575f5ffd5b806369a8c5f2146104ae5780637cc9e4f0146104d05780638456cb59146104e357806391358eff146104eb57806391d14854146104fe575f5ffd5b806331def01a116101c157806356e484551161018657806356e48455146104325780635c975abb14610456578063609a47461461046157806366e305fd146104745780636787785c14610487575f5ffd5b806331def01a146103a757806336568abe146103fc57806337271cc71461040f5780633f4ba83a146104175780633f6fff4e1461041f575f5ffd5b80632246444611610207578063224644461461032b578063248a9ca31461033e57806326a3cc9b1461036e5780632ef6d006146103815780632f2ff15d14610394575f5ffd5b806301ffc9a71461023857806304670ccb1461026057806309e69ede146102755780631153398114610318575b5f5ffd5b61024b610246366004611e29565b610635565b60405190151581526020015b60405180910390f35b61027361026e366004611e50565b61066b565b005b6102d6610283366004611e82565b600260208181525f9283526040928390208054600182015485518087019096529382015485526003820154928501929092526004015460ff80831694610100840482169462010000909404909116929186565b60408051961515875294151560208088019190915293151594860194909452606085019190915280516080850152015160a083015260c082015260e001610257565b610273610326366004611e9b565b61067b565b610273610339366004611e82565b6109a7565b61036061034c366004611e50565b5f9081526020819052604090206001015490565b604051908152602001610257565b61027361037c366004611e50565b610a78565b61027361038f366004611ecc565b610bb6565b6102736103a2366004611ef4565b610c79565b6103ec6103b5366004611ecc565b600360209081525f928352604080842090915290825290208054600182015460029092015490919060ff8082169161010090041684565b6040516102579493929190611f46565b61027361040a366004611ef4565b610ca3565b610273610cd6565b610273610ecc565b61027361042d366004611e50565b610ee1565b610445610440366004611e50565b610ff0565b604051610257959493929190611f82565b60015460ff1661024b565b61027361046f366004611ecc565b61103c565b61024b610482366004611e82565b611134565b6103607f000000000000000000000000000000000000000000000000000000000000000081565b61024b6104bc366004611e50565b60086020525f908152604090205460ff1681565b6102736104de366004611e82565b61119a565b61027361128c565b6102736104f9366004611fbe565b61129e565b61024b61050c366004611ef4565b61177b565b6103605f5160206120935f395f51905f5281565b6103605f81565b610360600a5481565b610360600b5481565b6103607f000000000000000000000000000000000000000000000000000000000000000081565b6102736117a3565b61036060065481565b61036060075481565b61036061058d366004611e50565b600c6020525f908152604090205481565b60095461024b9060ff1681565b6103607f000000000000000000000000000000000000000000000000000000000000000081565b61036060055481565b6102736105e9366004611ef4565b6117e5565b6106157f000000000000000000000000000000000000000000000000000000000000000081565b6040516001600160a01b039091168152602001610257565b610273611809565b5f6001600160e01b03198216637965db0b60e01b148061066557506301ffc9a760e01b6001600160e01b03198316145b92915050565b5f61067581611a96565b50600b55565b610683611aa0565b61068b611abb565b60095460ff16156106b75760405162461bcd60e51b81526004016106ae90611feb565b60405180910390fd5b6007545f9081526008602052604090205460ff16156107245760405162461bcd60e51b8152602060048201526024808201527f4c616e63657320656e6365727261646f732070617261206f206369636c6f20616044820152631d1d585b60e21b60648201526084016106ae565b335f908152600260205260409020805460ff1661077e5760405162461bcd60e51b81526020600482015260186024820152775574696c697a61646f72206e616f2072656769737461646f60401b60448201526064016106ae565b8054610100900460ff16156107ec5760405162461bcd60e51b815260206004820152602e60248201527f5574696c697a61646f72657320636f6e74656d706c61646f73206e616f20706f60448201526d64656d20646172206c616e63657360901b60648201526084016106ae565b5f831161083b5760405162461bcd60e51b815260206004820152601f60248201527f4f206c616e6365206465766520736572206d61696f7220717565207a65726f0060448201526064016106ae565b6108706001600160a01b037f000000000000000000000000000000000000000000000000000000000000000016333086611adf565b8260065f828254610881919061202c565b9250508190555082816004015f82825461089b919061202c565b9091555050604080516080810182526007548152600483015460208201529081018360028111156108ce576108ce611f1e565b81525f60209182018190523381526003825260408082206007548352835290819020835181559183015160018381019190915590830151600280840180549293909260ff191691849081111561092657610926611f1e565b021790555060609190910151600290910180549115156101000261ff001990921691909117905560075460405133907fd4b9182c3415bc6af502af189992ed111ca371194a99e5d1b29c52392210dc0290610984908790879061203f565b60405180910390a3506109a360015f5160206120b35f395f51905f5255565b5050565b5f5160206120935f395f51905f526109be81611a96565b6001600160a01b0382165f908152600260205260409020805460ff16610a225760405162461bcd60e51b81526020600482015260196024820152785574696c697a61646f72206e616f207265676973747261646f60381b60448201526064016106ae565b5f600282018190556003820180549190556040518181526001600160a01b038516907f58ddfbc1ce4796e7ab457d4f84a6743ca922c6be473ef648dfffecd5d33b73f2906020015b60405180910390a250505050565b5f5160206120935f395f51905f52610a8f81611a96565b60095460ff1615610ab25760405162461bcd60e51b81526004016106ae90611feb565b600754610ac090600161202c565b8214610b0e5760405162461bcd60e51b815260206004820152601c60248201527f4369636c6f7320646576656d207365722073657175656e63696169730000000060448201526064016106ae565b6007545f9081526008602052604090205460ff16610b835760405162461bcd60e51b815260206004820152602c60248201527f4665636865206f206369636c6f20617475616c20616e7465732064652061627260448201526b6972206f2070726f78696d6f60a01b60648201526084016106ae565b600782905560405182907f16ae2f9b3cb8b3312b810271924b6bece77a7bbedb1d827098eb6d0ee48e20fa905f90a25050565b5f610bc081611a96565b7f00000000000000000000000000000000000000000000000000000000000000006001600160a01b0316836001600160a01b031603610c605760405162461bcd60e51b815260206004820152603660248201527f4e616f2065207065726d697469646f2072657469726172206f20746f6b656e206044820152757072696e636970616c20646f20636f6e736f7263696f60501b60648201526084016106ae565b610c746001600160a01b0384163384611b15565b505050565b5f82815260208190526040902060010154610c9381611a96565b610c9d8383611b4a565b50505050565b6001600160a01b0381163314610ccc5760405163334bd91960e11b815260040160405180910390fd5b610c748282611bd9565b610cde611aa0565b335f908152600260205260409020600481015480610d3e5760405162461bcd60e51b815260206004820152601a60248201527f4e656e68756d206c616e6365207061726120726573676174617200000000000060448201526064016106ae565b8154610100900460ff1615610da15760405162461bcd60e51b8152602060048201526024808201527f56656e6365646f726573206e616f20706f64656d2072657469726172206f206c604482015263616e636560e01b60648201526084016106ae565b335f9081526003602090815260408083206007548452909152902060020154610100900460ff1615610e265760405162461bcd60e51b815260206004820152602860248201527f4c616e6365732076656e6365646f72657320666963616d2072657469646f73206044820152676e6f20636169786160c01b60648201526084016106ae565b5f82600401819055508060065f828254610e409190612053565b90915550610e7a90506001600160a01b037f0000000000000000000000000000000000000000000000000000000000000000163383611b15565b60075460405182815233907f0a3d3ac11cf502aaab4363b2a94047d52b45ee3c72068ff8f036cea2c899dae79060200160405180910390a35050610eca60015f5160206120b35f395f51905f5255565b565b5f610ed681611a96565b610ede611c42565b50565b5f5160206120935f395f51905f52610ef881611a96565b6007548214610f575760405162461bcd60e51b815260206004820152602560248201527f4170656e6173206f206369636c6f20617475616c20706f646520736572206665604482015264636861646f60d81b60648201526084016106ae565b5f8281526008602052604090205460ff1615610fad5760405162461bcd60e51b81526020600482015260156024820152744369636c6f206a612065737461206665636861646f60581b60448201526064016106ae565b5f82815260086020526040808220805460ff191660011790555183917f74d12e44562464f85a3a71177da95986fe35b34af49cb1536605ac4796d6f83491a25050565b60048181548110610fff575f80fd5b5f918252602090912060059091020180546001820154600283015460038401546004909401546001600160a01b0390931694509092909160ff1685565b5f5160206120935f395f51905f5261105381611a96565b6001600160a01b0383165f908152600260205260409020805460ff166110b75760405162461bcd60e51b81526020600482015260196024820152785574696c697a61646f72206e616f207265676973747261646f60381b60448201526064016106ae565b600281018054905f6110c883612066565b919050555082816002016001015f8282546110e3919061202c565b9091555050600281015460038201546040516001600160a01b038716927fbd2f45e6e94c217d21c90f28de3287b834749c4aa6beb0e88acafaa8d872e98492610a6a92918252602082015260400190565b6001600160a01b0381165f908152600260205260408120805460ff1680156111635750805462010000900460ff165b801561117657508054610100900460ff16155b801561118457506002810154155b8015611193575060095460ff16155b9392505050565b5f5160206120935f395f51905f526111b181611a96565b60095460ff16156111d45760405162461bcd60e51b81526004016106ae90611feb565b6001600160a01b0382165f9081526002602052604090205460ff161561123c5760405162461bcd60e51b815260206004820152601e60248201527f4f207574696c697a61646f72206a6120657374612072656769737461646f000060448201526064016106ae565b6001600160a01b0382165f81815260026020526040808220805462ff00ff191662010001179055517fe11711cd714e06fbbbea301a8e90822f2f2ea4808e37e3adf06038f33c53ff279190a25050565b5f61129681611a96565b610ede611c94565b5f5160206120935f395f51905f526112b581611a96565b6112bd611aa0565b60095460ff16156112e05760405162461bcd60e51b81526004016106ae90611feb565b6112e983611134565b6113455760405162461bcd60e51b815260206004820152602760248201527f4f207574696c697a61646f72206e616f206520656c65676976656c2070617261604482015266103b32b731b2b960c91b60648201526084016106ae565b600b546007545f908152600c6020526040902054106113b85760405162461bcd60e51b815260206004820152602960248201527f4c696d69746520646520636f6e74656d706c61636f657320646f206369636c6f604482015268206174696e6769646f60b81b60648201526084016106ae565b7f0000000000000000000000000000000000000000000000000000000000000000600554101561143d5760405162461bcd60e51b815260206004820152602a60248201527f46756e646f7320696e737566696369656e746573206e6f20636f66726520646f60448201526920636f6e736f7263696f60b01b60648201526084016106ae565b6001600160a01b0383165f908152600260205260408120805461ff001916610100178155600a8054919261147083612066565b90915550506007545f908152600c6020526040812080549161149183612066565b90915550600190508360018111156114ab576114ab611f1e565b036115895760048101548061151e5760405162461bcd60e51b815260206004820152603360248201527f5061727469636970616e7465206e616f20706f73737569206c616e636520617460448201527269766f20706172612065737465206369636c6f60681b60648201526084016106ae565b6001600160a01b0385165f90815260036020908152604080832060075484529091528120600201805461ff00191661010017905560068054839290611564908490612053565b925050819055508060055f82825461157c919061202c565b90915550505f6004830155505b7f000000000000000000000000000000000000000000000000000000000000000060055f8282546115ba9190612053565b9250508190555060046040518060a00160405280866001600160a01b0316815260200160075481526020017f0000000000000000000000000000000000000000000000000000000000000000815260200142815260200185600181111561162357611623611f1e565b90528154600180820184555f938452602093849020835160059093020180546001600160a01b0319166001600160a01b03909316929092178255928201518184015560408201516002820155606082015160038201556080820151600482018054939492939192909160ff19169083818111156116a2576116a2611f1e565b02179055506116fe9150506001600160a01b037f000000000000000000000000000000000000000000000000000000000000000016857f0000000000000000000000000000000000000000000000000000000000000000611b15565b600754846001600160a01b03167f76fd1e10f5342f22ae7db2f38586228b46b573fefa9118281281ce9f2032d0fd7f00000000000000000000000000000000000000000000000000000000000000008660405161175c92919061207e565b60405180910390a350610c7460015f5160206120b35f395f51905f5255565b5f918252602082815260408084206001600160a01b0393909316845291905290205460ff1690565b5f6117ad81611a96565b6009805460ff191660011790556040517f19aacb93339e19cc35c7d2cd9439dd8eacb8e91668a2caa80156690487923659905f90a150565b5f828152602081905260409020600101546117ff81611a96565b610c9d8383611bd9565b611811611aa0565b611819611abb565b60095460ff161561183c5760405162461bcd60e51b81526004016106ae90611feb565b335f908152600260205260409020805460ff166118965760405162461bcd60e51b81526020600482015260186024820152775574696c697a61646f72206e616f2072656769737461646f60401b60448201526064016106ae565b7f00000000000000000000000000000000000000000000000000000000000000008160010154106119155760405162461bcd60e51b8152602060048201526024808201527f546f646173206173206d656e73616c696461646573206a6120666f72616d20706044820152636167617360e01b60648201526084016106ae565b6002810154156119865760405162461bcd60e51b815260206004820152603660248201527f526567756c6172697a6520737561732070617263656c61732061747261736164604482015275617320636f6d20612061646d696e697374726163616f60501b60648201526084016106ae565b6119db6001600160a01b037f00000000000000000000000000000000000000000000000000000000000000001633307f0000000000000000000000000000000000000000000000000000000000000000611adf565b7f000000000000000000000000000000000000000000000000000000000000000060055f828254611a0c919061202c565b9091555050600181018054905f611a2283612066565b90915550506007546040517f0000000000000000000000000000000000000000000000000000000000000000815233907f366653c4cf6f896fe99dedabd9e7d0791b1ba2b434183dfdc3143014927c15859060200160405180910390a350610eca60015f5160206120b35f395f51905f5255565b610ede8133611ccf565b611aa8611d08565b60025f5160206120b35f395f51905f5255565b60015460ff1615610eca5760405163d93c066560e01b815260040160405180910390fd5b611aed848484846001611d37565b610c9d57604051635274afe760e01b81526001600160a01b03851660048201526024016106ae565b611b228383836001611da4565b610c7457604051635274afe760e01b81526001600160a01b03841660048201526024016106ae565b5f611b55838361177b565b611bd2575f838152602081815260408083206001600160a01b03861684529091529020805460ff19166001179055611b8a3390565b6001600160a01b0316826001600160a01b0316847f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a4506001610665565b505f610665565b5f611be4838361177b565b15611bd2575f838152602081815260408083206001600160a01b0386168085529252808320805460ff1916905551339286917ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b9190a4506001610665565b611c4a611e06565b6001805460ff191690557f5db9ee0a495bf2e6ff9c91a7834c1ba4fdd244a5e8aa4e537bd38aeae4b073aa335b6040516001600160a01b03909116815260200160405180910390a1565b611c9c611abb565b6001805460ff1916811790557f62e78cea01bee320cd4e420270b5ea74000d11b0c9f74754ebdbfc544b05a25833611c77565b611cd9828261177b565b6109a35760405163e2517d3f60e01b81526001600160a01b0382166004820152602481018390526044016106ae565b5f5160206120b35f395f51905f5254600203610eca57604051633ee5aeb560e01b815260040160405180910390fd5b6040516323b872dd60e01b5f8181526001600160a01b038781166004528616602452604485905291602083606481808c5af1925060015f51148316611d93578383151615611d87573d5f823e3d81fd5b5f883b113d1516831692505b604052505f60605295945050505050565b60405163a9059cbb60e01b5f8181526001600160a01b038616600452602485905291602083604481808b5af1925060015f51148316611dfa578383151615611dee573d5f823e3d81fd5b5f873b113d1516831692505b60405250949350505050565b60015460ff16610eca57604051638dfc202b60e01b815260040160405180910390fd5b5f60208284031215611e39575f5ffd5b81356001600160e01b031981168114611193575f5ffd5b5f60208284031215611e60575f5ffd5b5035919050565b80356001600160a01b0381168114611e7d575f5ffd5b919050565b5f60208284031215611e92575f5ffd5b61119382611e67565b5f5f60408385031215611eac575f5ffd5b82359150602083013560038110611ec1575f5ffd5b809150509250929050565b5f5f60408385031215611edd575f5ffd5b611ee683611e67565b946020939093013593505050565b5f5f60408385031215611f05575f5ffd5b82359150611f1560208401611e67565b90509250929050565b634e487b7160e01b5f52602160045260245ffd5b60038110611f4257611f42611f1e565b9052565b8481526020810184905260808101611f616040830185611f32565b821515606083015295945050505050565b60028110611f4257611f42611f1e565b6001600160a01b038616815260208101859052604081018490526060810183905260a08101611fb46080830184611f72565b9695505050505050565b5f5f60408385031215611fcf575f5ffd5b611fd883611e67565b9150602083013560028110611ec1575f5ffd5b602080825260139082015272436f6e736f7263696f20656e6365727261646f60681b604082015260600190565b634e487b7160e01b5f52601160045260245ffd5b8082018082111561066557610665612018565b828152604081016111936020830184611f32565b8181038181111561066557610665612018565b5f6001820161207757612077612018565b5060010190565b828152604081016111936020830184611f7256fe25cf2b509f2a7f322675b2a5322b182f44ad2c03ac941a0af17c9b178f5d5d5f9b779b17422d0df92223018b32b4d1fa46e071723d6817e2486d003becc55f00a264697066735822122042fcc383090e7af5da655f5e97a8243baa8e54551ddd0963f519cf5e90def8c864736f6c634300081e0033";

    public static final String FUNC_BACKEND_ROLE = "BACKEND_ROLE";

    public static final String FUNC_DEFAULT_ADMIN_ROLE = "DEFAULT_ADMIN_ROLE";

    public static final String FUNC_BIDFUNDBALANCE = "bidFundBalance";

    public static final String FUNC_BIDSBYCYCLE = "bidsByCycle";

    public static final String FUNC_CLOSECYCLE = "closeCycle";

    public static final String FUNC_CONSORTIUMFUNDBALANCE = "consortiumFundBalance";

    public static final String FUNC_CONTEMPLATEWINNER = "contemplateWinner";

    public static final String FUNC_CONTEMPLATEDCOUNT = "contemplatedCount";

    public static final String FUNC_CONTEMPLATIONS = "contemplations";

    public static final String FUNC_CREDITVALUE = "creditValue";

    public static final String FUNC_CURRENTCYCLE = "currentCycle";

    public static final String FUNC_CYCLECLOSED = "cycleClosed";

    public static final String FUNC_CYCLECONTEMPLATIONS = "cycleContemplations";

    public static final String FUNC_FINISHED = "finished";

    public static final String FUNC_FORCEFINISHCONSORTIUM = "forceFinishConsortium";

    public static final String FUNC_GETROLEADMIN = "getRoleAdmin";

    public static final String FUNC_GRANTROLE = "grantRole";

    public static final String FUNC_HASROLE = "hasRole";

    public static final String FUNC_INSTALLMENTVALUE = "installmentValue";

    public static final String FUNC_ISELIGIBLE = "isEligible";

    public static final String FUNC_MARKDELINQUENT = "markDelinquent";

    public static final String FUNC_MAXCONTEMPLATIONSPERCYCLE = "maxContemplationsPerCycle";

    public static final String FUNC_OPENCYCLE = "openCycle";

    public static final String FUNC_PARTICIPANTS = "participants";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_PAYINSTALLMENT = "payInstallment";

    public static final String FUNC_PLACEBID = "placeBid";

    public static final String FUNC_REGISTERPARTICIPANT = "registerParticipant";

    public static final String FUNC_REGULARIZEPARTICIPANT = "regularizeParticipant";

    public static final String FUNC_RENOUNCEROLE = "renounceRole";

    public static final String FUNC_RESCUEACCIDENTALERC20 = "rescueAccidentalERC20";

    public static final String FUNC_REVOKEROLE = "revokeRole";

    public static final String FUNC_SETMAXCONTEMPLATIONSPERCYCLE = "setMaxContemplationsPerCycle";

    public static final String FUNC_STABLECOIN = "stablecoin";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_TOTALMONTHS = "totalMonths";

    public static final String FUNC_UNPAUSE = "unpause";

    public static final String FUNC_WITHDRAWBID = "withdrawBid";

    public static final Event BIDPLACED_EVENT = new Event("BidPlaced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint8>() {}));
    ;

    public static final Event BIDWITHDRAWN_EVENT = new Event("BidWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event CONSORTIUMFINISHED_EVENT = new Event("ConsortiumFinished", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event CYCLECLOSED_EVENT = new Event("CycleClosed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event CYCLEOPENED_EVENT = new Event("CycleOpened", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event INSTALLMENTPAID_EVENT = new Event("InstallmentPaid", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event PARTICIPANTCONTEMPLATED_EVENT = new Event("ParticipantContemplated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint8>() {}));
    ;

    public static final Event PARTICIPANTDELINQUENT_EVENT = new Event("ParticipantDelinquent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event PARTICIPANTREGISTERED_EVENT = new Event("ParticipantRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event PARTICIPANTREGULARIZED_EVENT = new Event("ParticipantRegularized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event PAUSED_EVENT = new Event("Paused", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event ROLEADMINCHANGED_EVENT = new Event("RoleAdminChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event ROLEGRANTED_EVENT = new Event("RoleGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event ROLEREVOKED_EVENT = new Event("RoleRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event UNPAUSED_EVENT = new Event("Unpaused", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected ConsortiumGroup(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ConsortiumGroup(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ConsortiumGroup(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ConsortiumGroup(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<BidPlacedEventResponse> getBidPlacedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BIDPLACED_EVENT, transactionReceipt);
        ArrayList<BidPlacedEventResponse> responses = new ArrayList<BidPlacedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BidPlacedEventResponse typedResponse = new BidPlacedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.bidType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BidPlacedEventResponse getBidPlacedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BIDPLACED_EVENT, log);
        BidPlacedEventResponse typedResponse = new BidPlacedEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.bidType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BidPlacedEventResponse> bidPlacedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBidPlacedEventFromLog(log));
    }

    public Flowable<BidPlacedEventResponse> bidPlacedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BIDPLACED_EVENT));
        return bidPlacedEventFlowable(filter);
    }

    public static List<BidWithdrawnEventResponse> getBidWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BIDWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<BidWithdrawnEventResponse> responses = new ArrayList<BidWithdrawnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BidWithdrawnEventResponse typedResponse = new BidWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BidWithdrawnEventResponse getBidWithdrawnEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BIDWITHDRAWN_EVENT, log);
        BidWithdrawnEventResponse typedResponse = new BidWithdrawnEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BidWithdrawnEventResponse> bidWithdrawnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBidWithdrawnEventFromLog(log));
    }

    public Flowable<BidWithdrawnEventResponse> bidWithdrawnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BIDWITHDRAWN_EVENT));
        return bidWithdrawnEventFlowable(filter);
    }

    public static List<ConsortiumFinishedEventResponse> getConsortiumFinishedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CONSORTIUMFINISHED_EVENT, transactionReceipt);
        ArrayList<ConsortiumFinishedEventResponse> responses = new ArrayList<ConsortiumFinishedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ConsortiumFinishedEventResponse typedResponse = new ConsortiumFinishedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ConsortiumFinishedEventResponse getConsortiumFinishedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CONSORTIUMFINISHED_EVENT, log);
        ConsortiumFinishedEventResponse typedResponse = new ConsortiumFinishedEventResponse();
        typedResponse.log = log;
        return typedResponse;
    }

    public Flowable<ConsortiumFinishedEventResponse> consortiumFinishedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getConsortiumFinishedEventFromLog(log));
    }

    public Flowable<ConsortiumFinishedEventResponse> consortiumFinishedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CONSORTIUMFINISHED_EVENT));
        return consortiumFinishedEventFlowable(filter);
    }

    public static List<CycleClosedEventResponse> getCycleClosedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CYCLECLOSED_EVENT, transactionReceipt);
        ArrayList<CycleClosedEventResponse> responses = new ArrayList<CycleClosedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CycleClosedEventResponse typedResponse = new CycleClosedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CycleClosedEventResponse getCycleClosedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CYCLECLOSED_EVENT, log);
        CycleClosedEventResponse typedResponse = new CycleClosedEventResponse();
        typedResponse.log = log;
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CycleClosedEventResponse> cycleClosedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCycleClosedEventFromLog(log));
    }

    public Flowable<CycleClosedEventResponse> cycleClosedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CYCLECLOSED_EVENT));
        return cycleClosedEventFlowable(filter);
    }

    public static List<CycleOpenedEventResponse> getCycleOpenedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CYCLEOPENED_EVENT, transactionReceipt);
        ArrayList<CycleOpenedEventResponse> responses = new ArrayList<CycleOpenedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CycleOpenedEventResponse typedResponse = new CycleOpenedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CycleOpenedEventResponse getCycleOpenedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CYCLEOPENED_EVENT, log);
        CycleOpenedEventResponse typedResponse = new CycleOpenedEventResponse();
        typedResponse.log = log;
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CycleOpenedEventResponse> cycleOpenedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCycleOpenedEventFromLog(log));
    }

    public Flowable<CycleOpenedEventResponse> cycleOpenedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CYCLEOPENED_EVENT));
        return cycleOpenedEventFlowable(filter);
    }

    public static List<InstallmentPaidEventResponse> getInstallmentPaidEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INSTALLMENTPAID_EVENT, transactionReceipt);
        ArrayList<InstallmentPaidEventResponse> responses = new ArrayList<InstallmentPaidEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InstallmentPaidEventResponse typedResponse = new InstallmentPaidEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InstallmentPaidEventResponse getInstallmentPaidEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INSTALLMENTPAID_EVENT, log);
        InstallmentPaidEventResponse typedResponse = new InstallmentPaidEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<InstallmentPaidEventResponse> installmentPaidEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInstallmentPaidEventFromLog(log));
    }

    public Flowable<InstallmentPaidEventResponse> installmentPaidEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INSTALLMENTPAID_EVENT));
        return installmentPaidEventFlowable(filter);
    }

    public static List<ParticipantContemplatedEventResponse> getParticipantContemplatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PARTICIPANTCONTEMPLATED_EVENT, transactionReceipt);
        ArrayList<ParticipantContemplatedEventResponse> responses = new ArrayList<ParticipantContemplatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ParticipantContemplatedEventResponse typedResponse = new ParticipantContemplatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.winner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.cType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ParticipantContemplatedEventResponse getParticipantContemplatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PARTICIPANTCONTEMPLATED_EVENT, log);
        ParticipantContemplatedEventResponse typedResponse = new ParticipantContemplatedEventResponse();
        typedResponse.log = log;
        typedResponse.winner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.cycle = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.cType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<ParticipantContemplatedEventResponse> participantContemplatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getParticipantContemplatedEventFromLog(log));
    }

    public Flowable<ParticipantContemplatedEventResponse> participantContemplatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PARTICIPANTCONTEMPLATED_EVENT));
        return participantContemplatedEventFlowable(filter);
    }

    public static List<ParticipantDelinquentEventResponse> getParticipantDelinquentEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PARTICIPANTDELINQUENT_EVENT, transactionReceipt);
        ArrayList<ParticipantDelinquentEventResponse> responses = new ArrayList<ParticipantDelinquentEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ParticipantDelinquentEventResponse typedResponse = new ParticipantDelinquentEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.overdueInstallments = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.accumulatedPenalty = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ParticipantDelinquentEventResponse getParticipantDelinquentEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PARTICIPANTDELINQUENT_EVENT, log);
        ParticipantDelinquentEventResponse typedResponse = new ParticipantDelinquentEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.overdueInstallments = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.accumulatedPenalty = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<ParticipantDelinquentEventResponse> participantDelinquentEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getParticipantDelinquentEventFromLog(log));
    }

    public Flowable<ParticipantDelinquentEventResponse> participantDelinquentEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PARTICIPANTDELINQUENT_EVENT));
        return participantDelinquentEventFlowable(filter);
    }

    public static List<ParticipantRegisteredEventResponse> getParticipantRegisteredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PARTICIPANTREGISTERED_EVENT, transactionReceipt);
        ArrayList<ParticipantRegisteredEventResponse> responses = new ArrayList<ParticipantRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ParticipantRegisteredEventResponse typedResponse = new ParticipantRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ParticipantRegisteredEventResponse getParticipantRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PARTICIPANTREGISTERED_EVENT, log);
        ParticipantRegisteredEventResponse typedResponse = new ParticipantRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ParticipantRegisteredEventResponse> participantRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getParticipantRegisteredEventFromLog(log));
    }

    public Flowable<ParticipantRegisteredEventResponse> participantRegisteredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PARTICIPANTREGISTERED_EVENT));
        return participantRegisteredEventFlowable(filter);
    }

    public static List<ParticipantRegularizedEventResponse> getParticipantRegularizedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PARTICIPANTREGULARIZED_EVENT, transactionReceipt);
        ArrayList<ParticipantRegularizedEventResponse> responses = new ArrayList<ParticipantRegularizedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ParticipantRegularizedEventResponse typedResponse = new ParticipantRegularizedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.penaltyPaid = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ParticipantRegularizedEventResponse getParticipantRegularizedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PARTICIPANTREGULARIZED_EVENT, log);
        ParticipantRegularizedEventResponse typedResponse = new ParticipantRegularizedEventResponse();
        typedResponse.log = log;
        typedResponse.participant = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.penaltyPaid = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ParticipantRegularizedEventResponse> participantRegularizedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getParticipantRegularizedEventFromLog(log));
    }

    public Flowable<ParticipantRegularizedEventResponse> participantRegularizedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PARTICIPANTREGULARIZED_EVENT));
        return participantRegularizedEventFlowable(filter);
    }

    public static List<PausedEventResponse> getPausedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PAUSED_EVENT, transactionReceipt);
        ArrayList<PausedEventResponse> responses = new ArrayList<PausedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PausedEventResponse typedResponse = new PausedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PausedEventResponse getPausedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PAUSED_EVENT, log);
        PausedEventResponse typedResponse = new PausedEventResponse();
        typedResponse.log = log;
        typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPausedEventFromLog(log));
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PAUSED_EVENT));
        return pausedEventFlowable(filter);
    }

    public static List<RoleAdminChangedEventResponse> getRoleAdminChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<RoleAdminChangedEventResponse> responses = new ArrayList<RoleAdminChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleAdminChangedEventResponse getRoleAdminChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEADMINCHANGED_EVENT, log);
        RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleAdminChangedEventFromLog(log));
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEADMINCHANGED_EVENT));
        return roleAdminChangedEventFlowable(filter);
    }

    public static List<RoleGrantedEventResponse> getRoleGrantedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEGRANTED_EVENT, transactionReceipt);
        ArrayList<RoleGrantedEventResponse> responses = new ArrayList<RoleGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleGrantedEventResponse getRoleGrantedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEGRANTED_EVENT, log);
        RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleGrantedEventFromLog(log));
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEGRANTED_EVENT));
        return roleGrantedEventFlowable(filter);
    }

    public static List<RoleRevokedEventResponse> getRoleRevokedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROLEREVOKED_EVENT, transactionReceipt);
        ArrayList<RoleRevokedEventResponse> responses = new ArrayList<RoleRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoleRevokedEventResponse getRoleRevokedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROLEREVOKED_EVENT, log);
        RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
        typedResponse.log = log;
        typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoleRevokedEventFromLog(log));
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEREVOKED_EVENT));
        return roleRevokedEventFlowable(filter);
    }

    public static List<UnpausedEventResponse> getUnpausedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UNPAUSED_EVENT, transactionReceipt);
        ArrayList<UnpausedEventResponse> responses = new ArrayList<UnpausedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnpausedEventResponse typedResponse = new UnpausedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UnpausedEventResponse getUnpausedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UNPAUSED_EVENT, log);
        UnpausedEventResponse typedResponse = new UnpausedEventResponse();
        typedResponse.log = log;
        typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUnpausedEventFromLog(log));
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNPAUSED_EVENT));
        return unpausedEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> BACKEND_ROLE() {
        final Function function = new Function(FUNC_BACKEND_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> DEFAULT_ADMIN_ROLE() {
        final Function function = new Function(FUNC_DEFAULT_ADMIN_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> bidFundBalance() {
        final Function function = new Function(FUNC_BIDFUNDBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple4<BigInteger, BigInteger, BigInteger, Boolean>> bidsByCycle(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_BIDSBYCYCLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple4<BigInteger, BigInteger, BigInteger, Boolean>>(function,
                new Callable<Tuple4<BigInteger, BigInteger, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<BigInteger, BigInteger, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, BigInteger, BigInteger, Boolean>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> closeCycle(BigInteger cycle) {
        final Function function = new Function(
                FUNC_CLOSECYCLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(cycle)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> consortiumFundBalance() {
        final Function function = new Function(FUNC_CONSORTIUMFUNDBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> contemplateWinner(String winner, BigInteger cType) {
        final Function function = new Function(
                FUNC_CONTEMPLATEWINNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, winner), 
                new org.web3j.abi.datatypes.generated.Uint8(cType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> contemplatedCount() {
        final Function function = new Function(FUNC_CONTEMPLATEDCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple5<String, BigInteger, BigInteger, BigInteger, BigInteger>> contemplations(BigInteger param0) {
        final Function function = new Function(FUNC_CONTEMPLATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple5<String, BigInteger, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<String, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<String, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<String, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> creditValue() {
        final Function function = new Function(FUNC_CREDITVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> currentCycle() {
        final Function function = new Function(FUNC_CURRENTCYCLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> cycleClosed(BigInteger param0) {
        final Function function = new Function(FUNC_CYCLECLOSED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> cycleContemplations(BigInteger param0) {
        final Function function = new Function(FUNC_CYCLECONTEMPLATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> finished() {
        final Function function = new Function(FUNC_FINISHED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> forceFinishConsortium() {
        final Function function = new Function(
                FUNC_FORCEFINISHCONSORTIUM, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> getRoleAdmin(byte[] role) {
        final Function function = new Function(FUNC_GETROLEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> grantRole(byte[] role, String account) {
        final Function function = new Function(
                FUNC_GRANTROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> hasRole(byte[] role, String account) {
        final Function function = new Function(FUNC_HASROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> installmentValue() {
        final Function function = new Function(FUNC_INSTALLMENTVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isEligible(String participant) {
        final Function function = new Function(FUNC_ISELIGIBLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, participant)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> markDelinquent(String participant, BigInteger penaltyAmount) {
        final Function function = new Function(
                FUNC_MARKDELINQUENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, participant), 
                new org.web3j.abi.datatypes.generated.Uint256(penaltyAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> maxContemplationsPerCycle() {
        final Function function = new Function(FUNC_MAXCONTEMPLATIONSPERCYCLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> openCycle(BigInteger cycle) {
        final Function function = new Function(
                FUNC_OPENCYCLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(cycle)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple6<Boolean, Boolean, Boolean, BigInteger, DebtInfo, BigInteger>> participants(String param0) {
        final Function function = new Function(FUNC_PARTICIPANTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<DebtInfo>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<Boolean, Boolean, Boolean, BigInteger, DebtInfo, BigInteger>>(function,
                new Callable<Tuple6<Boolean, Boolean, Boolean, BigInteger, DebtInfo, BigInteger>>() {
                    @Override
                    public Tuple6<Boolean, Boolean, Boolean, BigInteger, DebtInfo, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<Boolean, Boolean, Boolean, BigInteger, DebtInfo, BigInteger>(
                                (Boolean) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue(), 
                                (Boolean) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (DebtInfo) results.get(4), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> paused() {
        final Function function = new Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> payInstallment() {
        final Function function = new Function(
                FUNC_PAYINSTALLMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> placeBid(BigInteger amount, BigInteger bidType) {
        final Function function = new Function(
                FUNC_PLACEBID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.generated.Uint8(bidType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerParticipant(String participant) {
        final Function function = new Function(
                FUNC_REGISTERPARTICIPANT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, participant)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> regularizeParticipant(String participant) {
        final Function function = new Function(
                FUNC_REGULARIZEPARTICIPANT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, participant)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceRole(byte[] role, String callerConfirmation) {
        final Function function = new Function(
                FUNC_RENOUNCEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, callerConfirmation)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> rescueAccidentalERC20(String tokenAddress, BigInteger amount) {
        final Function function = new Function(
                FUNC_RESCUEACCIDENTALERC20, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, tokenAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeRole(byte[] role, String account) {
        final Function function = new Function(
                FUNC_REVOKEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setMaxContemplationsPerCycle(BigInteger _max) {
        final Function function = new Function(
                FUNC_SETMAXCONTEMPLATIONSPERCYCLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_max)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> stablecoin() {
        final Function function = new Function(FUNC_STABLECOIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> supportsInterface(byte[] interfaceId) {
        final Function function = new Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> totalMonths() {
        final Function function = new Function(FUNC_TOTALMONTHS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawBid() {
        final Function function = new Function(
                FUNC_WITHDRAWBID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ConsortiumGroup load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ConsortiumGroup(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ConsortiumGroup load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ConsortiumGroup(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ConsortiumGroup load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ConsortiumGroup(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ConsortiumGroup load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ConsortiumGroup(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ConsortiumGroup> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String admin, String backend, String stablecoinAddress, BigInteger _creditValue, BigInteger _installmentValue, BigInteger _totalMonths, BigInteger _maxContemplationsPerCycle) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, admin), 
                new org.web3j.abi.datatypes.Address(160, backend), 
                new org.web3j.abi.datatypes.Address(160, stablecoinAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_creditValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_installmentValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalMonths), 
                new org.web3j.abi.datatypes.generated.Uint256(_maxContemplationsPerCycle)));
        return deployRemoteCall(ConsortiumGroup.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<ConsortiumGroup> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String admin, String backend, String stablecoinAddress, BigInteger _creditValue, BigInteger _installmentValue, BigInteger _totalMonths, BigInteger _maxContemplationsPerCycle) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, admin), 
                new org.web3j.abi.datatypes.Address(160, backend), 
                new org.web3j.abi.datatypes.Address(160, stablecoinAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_creditValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_installmentValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalMonths), 
                new org.web3j.abi.datatypes.generated.Uint256(_maxContemplationsPerCycle)));
        return deployRemoteCall(ConsortiumGroup.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ConsortiumGroup> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String admin, String backend, String stablecoinAddress, BigInteger _creditValue, BigInteger _installmentValue, BigInteger _totalMonths, BigInteger _maxContemplationsPerCycle) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, admin), 
                new org.web3j.abi.datatypes.Address(160, backend), 
                new org.web3j.abi.datatypes.Address(160, stablecoinAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_creditValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_installmentValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalMonths), 
                new org.web3j.abi.datatypes.generated.Uint256(_maxContemplationsPerCycle)));
        return deployRemoteCall(ConsortiumGroup.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<ConsortiumGroup> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String admin, String backend, String stablecoinAddress, BigInteger _creditValue, BigInteger _installmentValue, BigInteger _totalMonths, BigInteger _maxContemplationsPerCycle) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, admin), 
                new org.web3j.abi.datatypes.Address(160, backend), 
                new org.web3j.abi.datatypes.Address(160, stablecoinAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_creditValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_installmentValue), 
                new org.web3j.abi.datatypes.generated.Uint256(_totalMonths), 
                new org.web3j.abi.datatypes.generated.Uint256(_maxContemplationsPerCycle)));
        return deployRemoteCall(ConsortiumGroup.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class DebtInfo extends StaticStruct {
        public BigInteger overdueInstallments;

        public BigInteger accumulatedPenalty;

        public DebtInfo(BigInteger overdueInstallments, BigInteger accumulatedPenalty) {
            super(new org.web3j.abi.datatypes.generated.Uint256(overdueInstallments), 
                    new org.web3j.abi.datatypes.generated.Uint256(accumulatedPenalty));
            this.overdueInstallments = overdueInstallments;
            this.accumulatedPenalty = accumulatedPenalty;
        }

        public DebtInfo(Uint256 overdueInstallments, Uint256 accumulatedPenalty) {
            super(overdueInstallments, accumulatedPenalty);
            this.overdueInstallments = overdueInstallments.getValue();
            this.accumulatedPenalty = accumulatedPenalty.getValue();
        }
    }

    public static class BidPlacedEventResponse extends BaseEventResponse {
        public String participant;

        public BigInteger cycle;

        public BigInteger amount;

        public BigInteger bidType;
    }

    public static class BidWithdrawnEventResponse extends BaseEventResponse {
        public String participant;

        public BigInteger cycle;

        public BigInteger amount;
    }

    public static class ConsortiumFinishedEventResponse extends BaseEventResponse {
    }

    public static class CycleClosedEventResponse extends BaseEventResponse {
        public BigInteger cycle;
    }

    public static class CycleOpenedEventResponse extends BaseEventResponse {
        public BigInteger cycle;
    }

    public static class InstallmentPaidEventResponse extends BaseEventResponse {
        public String participant;

        public BigInteger cycle;

        public BigInteger amount;
    }

    public static class ParticipantContemplatedEventResponse extends BaseEventResponse {
        public String winner;

        public BigInteger cycle;

        public BigInteger amount;

        public BigInteger cType;
    }

    public static class ParticipantDelinquentEventResponse extends BaseEventResponse {
        public String participant;

        public BigInteger overdueInstallments;

        public BigInteger accumulatedPenalty;
    }

    public static class ParticipantRegisteredEventResponse extends BaseEventResponse {
        public String participant;
    }

    public static class ParticipantRegularizedEventResponse extends BaseEventResponse {
        public String participant;

        public BigInteger penaltyPaid;
    }

    public static class PausedEventResponse extends BaseEventResponse {
        public String account;
    }

    public static class RoleAdminChangedEventResponse extends BaseEventResponse {
        public byte[] role;

        public byte[] previousAdminRole;

        public byte[] newAdminRole;
    }

    public static class RoleGrantedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class RoleRevokedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class UnpausedEventResponse extends BaseEventResponse {
        public String account;
    }
}
