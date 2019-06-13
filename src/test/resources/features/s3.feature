Feature: S3 feature

  Scenario: S3 full work
    When the client call fileCount method, he receives 0
    And client call uploadFile method, with text "Hello"
    When the client call fileCount method, he receives 1
    And client call downloadFile method - lastFile
    When the client parse last download file, he receives "Hello"
    And client call deleteFile method - lastFile
    When the client call fileCount method, he receives 0
    And client call uploadFile method, with text "Some text"
    And client call getPreSignedUrl method - lastFile
    When client use last preSigned URL - download and parse file, he receives "Some text"
    When the client call fileCount method, he receives 1
    And client call deleteFiles method - lastFile
    When the client call fileCount method, he receives 0