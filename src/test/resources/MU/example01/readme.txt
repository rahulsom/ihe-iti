readme.txt - Update DocumentEntry example

Logistics

A known DocumentEntry exists in the metadata receiver (Document 
Registry, Document Recipient, etc.).  The Document Administrator actor:

Option 1) Is integrated with the Document Source actor that originally submitted the DocumentEntry.  It was submitted with UUID format 
id (entryUUID) attribute value and included the hash and size
attributes so 
the Document Administrator knows enough to formulate the update
submission. It is necessary to know the hash and size in this scenario
since they could have been left off a Provide and Register transaction
sent to a Document Repository.  Later when formulating the update, they
must be included. (note this is a XDS-centric issue where the 
Provide and Register goes to the Document Registry but the Update
goes directly to the Document Registry.  In the case of XDR the Provide and 
Register and the Update go directly to the Document Recipient.)

Option 2) Does not have knowledge of the details of the original submission
but gets a copy of the original DocumentEntry via query.

Scenario

The Document Administrator wished to update the classCode
attribute on the Document Entry.  This update will remove the old 
classCode and insert the new classCode.  No other changes are to be made. Note that the original DocumentEntry is literally the original version, version number 1. 

The update is constructed consisting of:

1) SubmissionSet object

2) Updated DocumentEntry object. The following attributes 
are updated:

* entryUUID (new UUID value or symbolic value). This is a new object
in the receiver and it must have a unique entryUUID. Note that this requires 
that all Classification and ExternalIdentifier elements within the 
ExtrinsicObject have their classifiedObject/registryObject
attributes updated to reference the new entryUUID (the parent). The 
example has value of Document01.  The registry will allocate a new 
UUID for this symbolic id upon receipt.

* lid - if DocumentEntry obtained through query, then the value
is already correct.  If it is the original submitted version then
lid must be set to the original id/entryUUID attribute value. The key
point here is that if starting from the original submission, the 
DocumentEntry in the original submission may have been submitted with
no lid attribute.  If lid was submitted it would have been equal to 
the entryUUID. 

* Classification object representing the old classCode is removed.

* New Classification object representing new classCode is generated
and inserted in a proper place. The proper place is after all Slot
Name, Description, and VersionInfo elements and before any 
ExternalIdentifier elements.

* If original was obtained via query, then it contains availabilityStatus and VersionInfo attributes.  These can be left in place since the 
Document Registry (or other recipient) is required to ignore them 
if included in the submission.

* Classification and ExternalIdentifier elements (from the query) have 
id and lid attributes, these must be changed.  I chose to replace the id 
values with symbolic values since I don't care what values they get
and simply remove the lid attributes.

* Classification and ExternalIdentifier elements have version
attributes with the default 1.1 value from the query results.
Metadata Update does not profile the use of the versions on these objects
so there left as found in the query result and will be overwritten at
the receiving end (two reasons not to bother changing or removing them).

* If:
   - this is a Document Source integrated with the Document Administrator
   - and this update is being generated from the Document Source's memory
of what was originally submitted (instead of being based on a query)
   - and the update is being sent to a Document Registry
then the Document Administrator must make sure that the hash and size
attributes are properly filled in. 

3) SubmissionSet to DocumentEntry HasMember association

* This requires the SubmissionSetStatus Slot since it is a submission of a
DocumentEntry.   

* Must have a Slot with name PreviousVersion containing a single value, 1.



Files

query_result.xml - response from a query containing a the single
DocumentEntry to be updated. Note that id (entryUUID) and lid
attributes hold the same value.  The version attribute has value 1.  Both
showing that the original documententry is a version 1 entry.

update.xml - submission of updated DocumentEntry.

